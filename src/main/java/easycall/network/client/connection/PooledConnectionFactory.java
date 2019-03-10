package easycall.network.client.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import easycall.network.client.nettyhandler.connection.ClientHeartBeatHandler;
import easycall.network.common.connection.Connection;
import easycall.network.common.connection.ConnectionFactoryAdapter;
import easycall.network.common.connection.DefaultTcpConnection;
import easycall.network.common.connection.management.ConnectionManager;
import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

/**
 * 池化连接工厂
 * @author 翁富鑫 2019/3/1 20:44
 */
public class PooledConnectionFactory extends ConnectionFactoryAdapter {

    // 读空闲最大30s，与服务端的读空闲时间保持相同
    private static final int readerIdleSeconds = 30;
    // 写空闲最大25s，保证最多在心跳发送超过5秒没收到回复后会关闭Channel
    private static final int writerIdleSeconds = 25;
    // disabled
    private static final int allIdleSeconds = 0;

    private ConnectionManager connectionManager;

    public PooledConnectionFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Connection buildConnection(String targetIp,  Integer targetPort, int timeout, TimeUnit unit) throws Exception {
        // 先从ConnectionManager中取连接
        Connection existConnection = connectionManager.getIdleConnectionByIpAndPort(targetIp,targetPort);
        if (existConnection == null) {
            // 创建一个连接
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(super.group)
                    .remoteAddress(targetIp, targetPort)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 写出数据分包处理器
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            // 连接检测
                            ch.pipeline().addLast(new IdleStateHandler(readerIdleSeconds,writerIdleSeconds,allIdleSeconds));
//                            // 处理IdleStateEvent，Channel、Connection管理Handler
//                            ch.pipeline().addLast(new ClientChannelConnectionManageHandler(connectionManager));
//                            ch.pipeline().addLast(new EchoClientHandler());
                            ch.pipeline().addLast(new ClientHeartBeatHandler(connectionManager));
                        }
                    })
                    .channel(NioSocketChannel.class)
                    // 连接的超时时间
                    .option(CONNECT_TIMEOUT_MILLIS,((Long)unit.toMillis(timeout)).intValue());
            ChannelFuture channelFuture;
            try {
                channelFuture = bootstrap.connect().sync();
                ChannelFuture closeFuture = channelFuture.channel().closeFuture();
                // 添加Channel关闭的监听器，关闭Channel后从ConnectionManager中移除
                closeFuture.addListener((future) -> {
                    System.out.println("PooledConnectionFactory --- removeConnection,Thread:" + Thread.currentThread().getName());
                    connectionManager.removeConnection(channelFuture.channel().id().asLongText());
                });
                Connection connection = new DefaultTcpConnection(channelFuture.channel());
                // 保存到Connection管理器中
                connectionManager.addConnection(connection);
                return connection;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return existConnection;
    }
}

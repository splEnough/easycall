package easycall.network.client.connection;

import easycall.loadbalance.LoadBalancer;
import easycall.network.client.nettyhandler.ClientResponseServiceDataHandler;
import easycall.network.client.nettyhandler.InputDataShow;
import easycall.network.common.handler.MagicCheckHandler;
import easycall.serviceconfig.client.RpcMessageManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import easycall.network.client.nettyhandler.ClientHeartBeatHandler;
import easycall.network.client.Connection;
import easycall.network.client.ConnectionFactoryAdapter;
import easycall.network.client.DefaultTcpConnection;
import easycall.network.client.management.ConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

/**
 * 池化连接工厂
 *
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
    private RpcMessageManager rpcMessageManager;

    /**
     * 负载均衡器
     */
    private LoadBalancer loadBalancer;

    public PooledConnectionFactory(ConnectionManager connectionManager, LoadBalancer loadBalancer, RpcMessageManager rpcMessageManager) {
        this.connectionManager = connectionManager;
        this.loadBalancer = loadBalancer;
        this.rpcMessageManager = rpcMessageManager;
    }

    @Override
    public Connection buildConnection(String targetIp, Integer targetPort, int timeout, TimeUnit unit) throws Exception {
        // 先从ConnectionManager中取连接
        Connection existConnection = connectionManager.getIdleConnectionByIpAndPort(targetIp, targetPort);
        if (existConnection == null) {
            List<ChannelHandler> channelHandlers = new ArrayList<>();
            // 创建一个连接
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(super.group)
                    .remoteAddress(targetIp, targetPort)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 写出数据分包处理器
                            LengthFieldPrepender lengthFieldPrepender = new LengthFieldPrepender(4);
                            // 数据解码器
                            LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(1048576,0,4,0,4);
                            // 魔数校验器
                            MagicCheckHandler magicCheckHandler = new MagicCheckHandler();
                            // 连接检测
                            IdleStateHandler idleStateHandler = new IdleStateHandler(readerIdleSeconds, writerIdleSeconds, allIdleSeconds);
                            // 心跳处理器
                            ClientHeartBeatHandler clientHeartBeatHandler = new ClientHeartBeatHandler(connectionManager);
                            // 响应数据处理器
                            ClientResponseServiceDataHandler clientResponseServiceDataHandler = new ClientResponseServiceDataHandler(rpcMessageManager);
                            // 添加到Pipeline中
                            ch.pipeline().addLast(lengthFieldPrepender);
                            ch.pipeline().addLast(lengthFieldBasedFrameDecoder);
                            ch.pipeline().addLast(magicCheckHandler);
                            ch.pipeline().addLast(idleStateHandler);
                            ch.pipeline().addLast(clientHeartBeatHandler);
                            ch.pipeline().addLast(clientResponseServiceDataHandler);
                            // 进站数据显示，用于测试
//                            ch.pipeline().addLast(new InputDataShow());

                            channelHandlers.add(lengthFieldPrepender);
                            channelHandlers.add(lengthFieldBasedFrameDecoder);
                            channelHandlers.add(magicCheckHandler);
                            channelHandlers.add(idleStateHandler);
                            channelHandlers.add(clientHeartBeatHandler);
                            channelHandlers.add(clientResponseServiceDataHandler);
                        }
                    })
                    .channel(NioSocketChannel.class)
                    // 连接的超时时间
                    .option(CONNECT_TIMEOUT_MILLIS, ((Long) unit.toMillis(timeout)).intValue());
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
                // ChannelHandler保存在Connection对象中
                connection.addChannelHandlers(channelHandlers);
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

    @Override
    public Connection buildTargetServiceConnection(String serviceName, String version, Integer targetPort, int timeout, TimeUnit unit) throws Exception {
        // 经过负载均衡
        String ip = this.loadBalancer.next(serviceName, version);
        return this.buildConnection(ip, targetPort, timeout, unit);
    }
}

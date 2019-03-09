package wfx.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import wfx.network.server.nettyhandler.EchoServerHandler;
import wfx.network.server.nettyhandler.IdleChannelCloseHandler;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

/**
 * 默认的服务端启动器
 * @author 翁富鑫 2019/3/3 9:57
 */
public class DefaultNioServerStarter extends ServerStarterAdapter {

    // 启动监听的端口
    private Integer port;
    // 初始化器
    private ServerInitializer serverInitializer;
    // 5秒的绑定超时
    private Integer bindTimeout = 5;

    // 空闲检测参数
    private int readerIdleSeconds = 8;
    // disabled
    private int writerIdleSeconds = 0;
    // disabled
    private int allIdleSeconds = 0;

    public DefaultNioServerStarter(ServerInitializer serverInitializer) {
        this.serverInitializer = serverInitializer;
        this.port = Integer.parseInt(serverInitializer.getInitProperties().getProperty("port"));
    }

    @Override
    public void start() throws Exception{
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(super.group)
                .localAddress(port)
                .option(CONNECT_TIMEOUT_MILLIS, Long.valueOf(TimeUnit.SECONDS.toMillis(bindTimeout)).intValue())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 数据解码器
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576,0,4,0,4));
                        // 空闲检测
                        ch.pipeline().addLast(new IdleStateHandler(readerIdleSeconds,writerIdleSeconds,allIdleSeconds));
                        // 关闭空闲连接
                        ch.pipeline().addLast(new IdleChannelCloseHandler());
                        ch.pipeline().addLast(new EchoServerHandler());
                    }
                });
        try {
            serverBootstrap.bind().sync();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        group.shutdownGracefully();
    }
}

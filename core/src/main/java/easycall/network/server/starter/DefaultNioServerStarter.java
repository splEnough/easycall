package easycall.network.server.starter;

import easycall.initconfig.ServerInitializer;
import easycall.network.common.handler.MagicCheckHandler;
import easycall.network.server.nettyhandler.IdleChannelCloseHandler;
import easycall.network.server.nettyhandler.RequestDataHandlerDispatcher;
import easycall.serviceconfig.server.RpcProviderManager;
import easycall.thread.ExecutorManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

/**
 * 默认的服务端TCP监听启动器
 * @author 翁富鑫 2019/3/3 9:57
 */
public class DefaultNioServerStarter extends ServerStarterAdapter {

    // 启动监听的端口
    private Integer port;
    // 初始化器
    private ServerInitializer serverInitializer;
    // 5秒的绑定超时
    private Integer bindTimeout = 5;

    // 空闲检测参数，与客户端的读空闲超时参数保持一致
    private int readerIdleSeconds = 30;
    // disabled
    private int writerIdleSeconds = 0;
    // disabled
    private int allIdleSeconds = 0;

    /**
     * 任务执行管理器
     */
    private ExecutorManager executorManager;

    private RpcProviderManager rpcProviderManager;

    public DefaultNioServerStarter(ServerInitializer serverInitializer, ExecutorManager executorManager, RpcProviderManager rpcProviderManager) {
        this.serverInitializer = serverInitializer;
        this.port = (Integer)(serverInitializer.getInitProperties().get("port"));
        this.executorManager = executorManager;
        this.rpcProviderManager = rpcProviderManager;
    }

    AtomicBoolean started = new AtomicBoolean();

    @Override
    public void start() throws Exception{
        if (started.compareAndSet(false , true)) {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(super.bossGroup, super.childGroup)
                    .localAddress(port)
                    .option(CONNECT_TIMEOUT_MILLIS, Long.valueOf(TimeUnit.SECONDS.toMillis(bindTimeout)).intValue())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 写出数据编码器
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            // 数据解码器
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                            // 魔数校验器
                            ch.pipeline().addLast(new MagicCheckHandler());
                            // 空闲检测
                            ch.pipeline().addLast(new IdleStateHandler(readerIdleSeconds, writerIdleSeconds, allIdleSeconds));
                            // 关闭空闲连接
                            ch.pipeline().addLast(new IdleChannelCloseHandler());
                            // 请求数据处理器
                            ch.pipeline().addLast(new RequestDataHandlerDispatcher(executorManager, rpcProviderManager, serverInitializer));
                        }
                    });
            try {
                serverBootstrap.bind().sync();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

}

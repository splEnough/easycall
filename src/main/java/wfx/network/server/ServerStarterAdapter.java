package wfx.network.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.IOException;

/**
 * ServerStarter直接继承这个类
 * @author 翁富鑫 2019/3/3 16:05
 */
public abstract class ServerStarterAdapter implements ServerStarter {

    /**
     * 供子类使用
     */
    protected EventLoopGroup group = new NioEventLoopGroup();

    @Override
    public void close() throws IOException {
        group.shutdownGracefully();
    }
}

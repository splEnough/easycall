package easycall.network.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.IOException;

/**
 * ServerStarter直接继承这个类
 * @author 翁富鑫 2019/3/3 16:05
 */
public abstract class ServerStarterAdapter implements ServerStarter {

    /**
     * bossGroup
     */
    protected EventLoopGroup bossGroup = new NioEventLoopGroup();

    protected EventLoopGroup childGroup = new NioEventLoopGroup(12);



    @Override
    public void close() throws IOException {
        bossGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }
}

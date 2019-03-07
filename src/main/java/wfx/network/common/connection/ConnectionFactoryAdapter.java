package wfx.network.common.connection;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.Closeable;
import java.io.IOException;

/**
 * 连接工厂适配器，提供EventLoopGroup及关闭操作
 * @author 翁富鑫 2019/3/3 16:02
 */
public abstract class ConnectionFactoryAdapter implements ConnectionFactory {

    /**
     * 供子类使用
     */
    protected EventLoopGroup group = new NioEventLoopGroup();

    @Override
    public void close() throws IOException {
        group.shutdownGracefully();
    }
}

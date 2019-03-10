package easycall.network.client.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import easycall.network.common.connection.Connection;
import easycall.network.common.connection.ConnectionFactoryAdapter;
import easycall.network.common.connection.DefaultTcpConnection;

import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

/**
 * 即创即用的连接工厂
 * @author 翁富鑫 2019/3/1 16:24
 */
public class InstantConnectionFactory extends ConnectionFactoryAdapter {

    @Override
    public Connection buildConnection(String targetIp,  Integer targetPort, int timeout, TimeUnit unit) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(super.group)
                .remoteAddress(targetIp, targetPort)
                .channel(NioSocketChannel.class)
                .option(CONNECT_TIMEOUT_MILLIS, ((Long)unit.toMillis(timeout)).intValue());
        ChannelFuture channelFuture;
        try {
            channelFuture = bootstrap.connect().sync();
            return new DefaultTcpConnection(channelFuture.channel());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}

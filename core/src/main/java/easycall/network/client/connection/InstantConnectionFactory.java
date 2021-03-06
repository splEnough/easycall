package easycall.network.client.connection;

import easycall.network.client.Connection;
import easycall.network.client.ConnectionFactoryAdapter;
import easycall.network.client.DefaultTcpConnection;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;

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

    @Override
    public Connection buildTargetServiceConnection(String serviceName, String version, Integer targetPort, int timeout, TimeUnit unit) throws Exception {

        return null;
    }

}

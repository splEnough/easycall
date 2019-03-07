package wfx.network.common.connection;

import io.netty.channel.Channel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 默认的Tcp连接信息
 * @author 翁富鑫 2019/3/1 16:21
 */
public class DefaultTcpConnection implements Connection{

    private String sourceIp;
    private String targetIp;
    private Integer targetPort;

    private Channel connectionChannel;

    public DefaultTcpConnection(Channel connectionChannel) {
        this.connectionChannel = connectionChannel;
        InetSocketAddress address = (InetSocketAddress) connectionChannel.remoteAddress();
        this.targetIp = address.getHostString();
        this.targetPort = address.getPort();
    }

    @Override
    public String getSourceIp() {
        return sourceIp;
    }

    @Override
    public String getTargetIp() {
        return targetIp;
    }

    @Override
    public Integer getTargetPort() {
        return targetPort;
    }

    @Override
    public Channel getConnectionChannel() {
        return connectionChannel;
    }

    @Override
    public void close() throws IOException {
        connectionChannel.closeFuture();
    }

    @Override
    public boolean equals(Object obj) {
        // 表示两个机器之间的连接：目标ip和port同时相等，Channel不一定相等，因为两台机器之间可以有多个连接
        Connection target = (Connection)obj;
        return (this.targetPort.equals(target.getTargetPort()) && this.targetIp.equals(target.getTargetIp()));
    }
}

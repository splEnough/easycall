package easycall.network.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的Tcp连接信息
 * @author 翁富鑫 2019/3/1 16:21
 */
public class DefaultTcpConnection implements Connection {

    private String sourceIp;
    private String targetIp;
    private Integer targetPort;
    /**
     * 当前连接是否以已经关闭
     */
    private boolean close = false;

    /**
     * 当前连接对应的Channel
     */
    private Channel connectionChannel;

    /**
     * 当前连接上次写数据的时间戳，ms
     */
    private volatile Long lastWriteTime;

    /**
     * 当前Connection中的ChannelHandler
     */
    private List<ChannelHandler> channelHandlers;

    public DefaultTcpConnection(Channel connectionChannel) {
        this.connectionChannel = connectionChannel;
        InetSocketAddress address = (InetSocketAddress) connectionChannel.remoteAddress();
        this.targetIp = address.getHostString();
        this.targetPort = address.getPort();
        // 初始化的时候保存时间
        this.lastWriteTime = System.currentTimeMillis();
        this.channelHandlers = new ArrayList<>();
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
    public String getConnectionId() {
        return connectionChannel.id().asLongText();
    }

    @Override
    public Long getLastWriteTime() {
        return lastWriteTime;
    }

    @Override
    public void resetLastWriteTime(long timeMills) {
        this.lastWriteTime = timeMills;
    }

    @Override
    public boolean isClose() {
        return close;
    }

    @Override
    public List<ChannelHandler> getAllChannelHandlers() {
        return this.channelHandlers;
    }

    @Override
    public void addChannelHandler(ChannelHandler channelHandler) {
        this.channelHandlers.add(channelHandler);
    }

    @Override
    public void addChannelHandlers(List<ChannelHandler> channelHandlers) {
        this.channelHandlers.addAll(channelHandlers);
    }

    @Override
    public void close() throws IOException {
        close = true;
        connectionChannel.close();
        for (ChannelHandler channelHandler : channelHandlers) {
            if (channelHandler instanceof Closeable) {
                // 释放掉每个ChannelHandler中的资源
                ((Closeable) channelHandler).close();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        // 表示两个机器之间的连接：目标ip和port同时相等，Channel不一定相等，因为两台机器之间可以有多个连接
        Connection target = (Connection)obj;
        return (this.targetPort.equals(target.getTargetPort()) && this.targetIp.equals(target.getTargetIp()));
    }
}

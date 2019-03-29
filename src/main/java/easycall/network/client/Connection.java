package easycall.network.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.io.Closeable;
import java.util.List;

/**
 * 当前主机与其他主机所建立的连接
 * @author 翁富鑫 2019/3/1 16:18
 */
public interface Connection extends Closeable{
    /**
     * 获取发起连接请求端的ip
     * @return
     */
    String getSourceIp();

    /**
     * 获取目标端ip
     * @return
     */
    String getTargetIp();

    /**
     * 获取目标端口
     * @return
     */
    Integer getTargetPort();

    /**
     * 获取当前连接的Channel
     * @return
     */
    Channel getConnectionChannel();

    /**
     * 获取当前连接的id
     */
    String getConnectionId();

    /**
     * 获取当前连接上次写数据的时间
     */
    Long getLastWriteTime();

    /**
     * 重置上次写数据的时间
     */
    void resetLastWriteTime(long timeMills);

    /**
     * 是否已经关闭
     * @return 已经关闭返回true
     */
    boolean isClose();

    /**
     * 获取所有的Netty ChannelHandler
     */
    List<ChannelHandler> getAllChannelHandlers() ;

    /**
     * 添加一个ChannelHandler
     * @param channelHandler
     */
    void addChannelHandler(ChannelHandler channelHandler) ;

    /**
     * 添加多个ChannelHandler
     * @param channelHandlers
     */
    void addChannelHandlers(List<ChannelHandler> channelHandlers);

}

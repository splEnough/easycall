package wfx.network.common.connection;

import io.netty.channel.Channel;

import java.io.Closeable;

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
}

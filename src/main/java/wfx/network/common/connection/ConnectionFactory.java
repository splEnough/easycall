package wfx.network.common.connection;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取连接
 * @author 翁富鑫 2019/3/1 16:16
 */
public interface ConnectionFactory extends Closeable{
    /**
     * 获取与目标主机的连接
     * TODO 实现连接重试功能
     * @param targetIp 目标主机的连接
     * @param targetPort 目标主机的端口
     * @param timeout 超时时间
     * @param unit 超时时间的单位
     * @return
     */
    Connection buildConnection(String targetIp,  Integer targetPort, int timeout, TimeUnit unit) throws Exception;
}

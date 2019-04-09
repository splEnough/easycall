package easycall.registercenter.server;

import java.io.Closeable;

/**
 * 服务端的服务注册器
 * @author 翁富鑫 2019/4/9 16:25
 */
public interface Register extends Closeable{

    /**
     * 启动注册中心
     */
    void start();

    /**
     * 注册一个服务
     * @param serviceName 服务名
     * @param version 服务的版本
     * @param ip 提供服务的ip
     */
    boolean registerService(String serviceName , String version , String ip);

    /**
     * 注册一个服务，默认为当前主机的ip
     * @param serviceName 服务名
     * @param version 服务的版本
     * @return 成功返回true，否则返回false
     */
    boolean registerService(String serviceName, String version);

    /**
     * 取消注册一个服务
     * @param serviceName 要取消的服务名
     * @param version 要取消的服务的版本
     * @return
     */
    boolean unRegisterService(String serviceName , String version) ;
}

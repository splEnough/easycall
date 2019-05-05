package easycall.registercenter;

import java.io.Closeable;
import java.util.List;

/**
 * 注册中心的客户端，完成服务端的服务注册和客户端的服务订阅
 * @author 翁富鑫 2019/3/24 16:41
 */
public interface RegisterCenterClient extends Closeable {

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
    boolean registerService(String serviceName, String version, String ip);

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
    boolean unRegisterService(String serviceName, String version) ;

    /**
     * 订阅一个服务，返回提供当前服务的服务器的ip地址列表，这个接口的返回数据应该是实时变化的
     * @param serviceName 要订阅的服务名
     * @param version 要订阅的版本号
     * @return 提供服务的ip地址列表
     */
    List<String> subscribeService(String serviceName, String version);

}

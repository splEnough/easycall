package easycall.registercenter.client;

import java.io.Closeable;
import java.util.Set;

/**
 * 服务消费方，用于服务订阅
 * @author 翁富鑫 2019/4/8 19:56
 */
public interface Subscriber extends Closeable{

    /**
     * 启动注册中心
     */
    void start();

    /**
     * 订阅一个服务，返回提供当前服务的服务器的ip地址列表，这个接口的返回数据应该是实时变化的
     * @param serviceName 要订阅的服务名
     * @param version 要订阅的版本号
     * @return 提供服务的ip地址列表
     */
    Set<String> subscribeService(String serviceName, String version);

}

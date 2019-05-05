package easycall.serviceconfig.client;

import java.util.Map;

/**
 * 客户端RPCService代理容器
 * @author 翁富鑫 2019/3/28 10:37
 */
public interface RpcConsumerProxyContainer {

//    /**
//     * 通过serviceName和version获取代理，如果不存在则创建一个
//     * @param serviceName 业务名
//     * @param version 版本号
//     */
//    RpcConsumerProxy getProxyByName(String serviceName ,String version) ;

    /**
     * 通过接口类型获取到代理对象
     * @param cls 接口类型
     */
    RpcConsumerProxy getProxyByInterfaceType(Class<?> cls, Map<String, Object> paramMap);
}

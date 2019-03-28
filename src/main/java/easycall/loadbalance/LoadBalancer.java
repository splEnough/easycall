package easycall.loadbalance;

import easycall.exception.RpcServiceNotFoundException;

/**
 * 负载均衡器
 * @author 翁富鑫 2019/3/28 10:58
 */
public interface LoadBalancer {
    /**
     * 通过业务名和版本号选择一个服务端ip
     * @param serviceName 要调用的业务名
     * @param version 要调用的版本号
     * @return
     */
    String next(String serviceName , String version) throws RpcServiceNotFoundException;
}

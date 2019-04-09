package easycall.loadbalance;

import easycall.exception.RpcServiceNotFoundException;
import easycall.registercenter.RegisterCenterClient;
import easycall.registercenter.client.Subscriber;

import java.util.List;
import java.util.Set;

/**
 * @author 翁富鑫 2019/3/28 14:40
 */
public abstract class LoadBalancerAdapter implements LoadBalancer {

    /**
     * 注册中心客户端
     */
    protected Subscriber subscriber;

    protected LoadBalancerAdapter(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public String next(String service, String version) throws RpcServiceNotFoundException{
        Set<String> ipSet = this.subscriber.subscribeService(service , version);
        if (ipSet == null || ipSet.size() == 0) {
            throw new RpcServiceNotFoundException("服务不存在");
        }
        return next0(service , version, ipSet);
    }

    abstract String next0(String service, String version, Set<String> ipList);
}

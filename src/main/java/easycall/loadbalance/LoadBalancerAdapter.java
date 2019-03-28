package easycall.loadbalance;

import easycall.exception.RpcServiceNotFoundException;
import easycall.registercenter.RegisterCenterClient;

import java.util.List;

/**
 * @author 翁富鑫 2019/3/28 14:40
 */
public abstract class LoadBalancerAdapter implements LoadBalancer {

    /**
     * 注册中心客户端
     */
    protected RegisterCenterClient registerCenterClient;

    protected LoadBalancerAdapter(RegisterCenterClient registerCenterClient) {
        this.registerCenterClient = registerCenterClient;
    }

    public String next(String service, String version) throws RpcServiceNotFoundException{
        List<String> ipList = this.registerCenterClient.subscribeService(service , version);
        if (ipList == null || ipList.size() == 0) {
            throw new RpcServiceNotFoundException("服务不存在");
        }
        return next0(service , version, ipList);
    }

    abstract String next0(String service, String version, List<String> ipList);
}

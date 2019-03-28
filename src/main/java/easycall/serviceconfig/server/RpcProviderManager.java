package easycall.serviceconfig.server;

import easycall.registercenter.RegisterCenterClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * RpcProvider管理器
 * @author 翁富鑫 2019/3/25 11:01
 */
public class RpcProviderManager {

    /**
     * 保存所有的RpcProvider，key为serviceName + "-" + version ，value为一个RpcProvider的实例
     */
    private Map<String, RPCProvider<?>> providerMap;

    /**
     * 注册中心
     */
    private RegisterCenterClient registerCenterClient;

    public RpcProviderManager(RegisterCenterClient registerCenterClient) {
        this.registerCenterClient = registerCenterClient;
        providerMap = new HashMap<>();
    }

    public RPCProvider<?> getProviderByServiceName(String serviceName, String version) {
        String key = serviceName + "-" + version;
        return providerMap.get(key);
    }

    /**
     * 发布服务
     * @param provider
     */
    public void exportProvider(RPCProvider provider) {
        this.registerCenterClient.registerService(provider.getServiceName() , provider.getVersion());
        String key = provider.getServiceName() + "-" + provider.getVersion();
        providerMap.put(key , provider);
    }

    /**
     * 取消发布一个服务
     */
    public void unExportProvider(String serviceName ,String version) {
        this.registerCenterClient.unRegisterService(serviceName , version);
        String key = serviceName + "-" + version;
        this.providerMap.remove(key);
    }

    /**
     * 获取所有的服务名
     */
    public Set<String> getAllServiceNames() {
        return providerMap.keySet();
    }

}
package easycall.service.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RpcProvider的管理器
 * @author 翁富鑫 2019/3/25 11:01
 */
public class RpcProviderManager {

    /**
     * 保存所有的RpcProvider，key为serviceName + "-" + version ，value为一个RpcProvider的实例
     */
    private Map<String, RPCProvider<?>> providerMap;

    public RpcProviderManager() {
        providerMap = new HashMap<>();
    }

    public RPCProvider<?> getProviderByServiceName(String serviceName, String version) {
        String key = serviceName + "-" + version;
        return providerMap.get(key);
    }

    public void addProvider(String serviceName , String version , RPCProvider provider) {
        String key = serviceName + "-" + version;
        providerMap.put(key , provider);
    }

    public Set<String> getAllServiceNames() {
        return providerMap.keySet();
    }

}

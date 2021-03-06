package easycall.serviceconfig.client;

import easycall.exception.ExportTypeException;
import easycall.initconfig.ClientParam;
import easycall.network.client.ConnectionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的客户端RPCService代理类容器
 * @author 翁富鑫 2019/3/28 10:43
 */
public class DefaultRpcConsumerProxyContainer implements RpcConsumerProxyContainer {

    /**
     * 保存所有的代理类，key为代理的服务serviceName+"-"+version
     */
    private Map<String, RpcConsumerProxy> consumerProxyMap = new ConcurrentHashMap<>();
    private ClientParam clientParam;
    private ConnectionFactory connectionFactory;
    private RpcMessageManager rpcMessageManager;

    public DefaultRpcConsumerProxyContainer(ClientParam clientParam, ConnectionFactory connectionFactory, RpcMessageManager rpcMessageManager) {
        this.clientParam = clientParam;
        this.connectionFactory = connectionFactory;
        this.rpcMessageManager = rpcMessageManager;
    }

//    @Override
//    public RpcConsumerProxy getProxyByName(String serviceName, String version) {
//        String key = key(serviceName , version);
//        RpcConsumerProxy result = consumerProxyMap.get(key);
//        if (result == null) {
//            synchronized (key) {
//                // DC
//                if (consumerProxyMap.get(key) != null) {
//                    return consumerProxyMap.get(key);
//                }
//                result = new RpcConsumerProxy(connectionFactory,serviceName,version,clientInitializer,rpcMessageManager);
//                consumerProxyMap.put(key, result);
//                return result;
//            }
//        }
//        return result;
//    }

    private String key(String serviceName , String version) {
        return serviceName + "-" + version;
    }

    @Override
    public RpcConsumerProxy getProxyByInterfaceType(Class<?> cls, Map<String, Object> paramMap) {
        if (!cls.isInterface()) {
            throw new ExportTypeException("参数必须是一个接口");
        }
        String serviceName = cls.getTypeName();
        if (paramMap.get("serviceName") != null) {
            serviceName = (String) paramMap.get("serviceName");
        }
        String version = clientParam.getVersion();
        if (paramMap.get("version") != null) {
            version = (String) paramMap.get("version");
        }
        String key = key(serviceName, version);
        long timeout = clientParam.getRpcTimeout();
        if (paramMap.get("timeout") != null) {
            timeout = (long) paramMap.get("timeout");
        }
        RpcConsumerProxy rpcConsumerProxy = new RpcConsumerProxy(connectionFactory, serviceName, version,
                clientParam, rpcMessageManager, timeout, cls);
        consumerProxyMap.put(key, rpcConsumerProxy);
        return rpcConsumerProxy;
    }
}

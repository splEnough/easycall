package easycall.serviceconfig.client;

import easycall.config.ClientInitializer;
import easycall.exception.ExportTypeException;
import easycall.network.common.connection.ConnectionFactory;

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
    private ClientInitializer clientInitializer;
    private ConnectionFactory connectionFactory;
    private RpcMessageManager rpcMessageManager;

    public DefaultRpcConsumerProxyContainer(ClientInitializer clientInitializer, ConnectionFactory connectionFactory, RpcMessageManager rpcMessageManager) {
        this.clientInitializer = clientInitializer;
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
    public RpcConsumerProxy getProxyByInterfaceType(Class<?> cls) {
        if (!cls.isInterface()) {
            throw new ExportTypeException("参数必须是一个接口");
        }
        String serviceName = cls.getTypeName();
        String version = (String)clientInitializer.getInitialParam("version");
        String key = key(serviceName, version);
        RpcConsumerProxy rpcConsumerProxy = new RpcConsumerProxy(connectionFactory, serviceName, version,
                clientInitializer, rpcMessageManager, (long) clientInitializer.getInitialParam("rpcTimeout"), cls);
        consumerProxyMap.put(key, rpcConsumerProxy);
        return rpcConsumerProxy;
    }
}
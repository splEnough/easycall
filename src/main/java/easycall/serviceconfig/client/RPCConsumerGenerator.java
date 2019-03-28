package easycall.serviceconfig.client;

import java.lang.reflect.Proxy;

/**
 * @author 翁富鑫 2019/3/28 10:02
 */
public class RPCConsumerGenerator {
    public static Object getProxy(Class<?> interfaceClass, RpcConsumerProxy rpcConsumerProxy) {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, rpcConsumerProxy);
    }
}

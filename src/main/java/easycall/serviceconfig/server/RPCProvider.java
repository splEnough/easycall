package easycall.serviceconfig.server;

import java.util.concurrent.ExecutorService;

/**
 * T 当前RPC服务指定的接个RPC服务提供方的封装信口
 * @author 翁富鑫 2019/3/24 14:34
 */
public class RPCProvider<T>{

    /**
     * 接口实现类的引用
     */
    private T rpcServiceObject ;

    /**
     * 当前的服务提供方对应的业务名
     */
    private String serviceName;

    /**
     * 当前服务提供方的对应的服务接口全限定名
     */
    private String interfaceName;

    /**
     * 当前的服务提供方的版本
     */
    private String version;

    /**
     * 自定义线程池，若存在则不为null
     */
    private ExecutorService executorService;

    /**
     * 当前服务提供方的代理类对象
     */
    private T proxyObject;

    public T getProxyObject() {
        if (proxyObject == null) {
            RpcServiceProxy<T> rpcServiceProxy = new RpcServiceProxy(this.rpcServiceObject);
            this.proxyObject = rpcServiceProxy.getProxyInstance();
        }
        return proxyObject;
    }

    public RPCProvider() {
    }

    public T getRpcServiceObject() {
        return rpcServiceObject;
    }

    public void setRpcServiceObject(T rpcServiceObject) {
        this.rpcServiceObject = rpcServiceObject;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

}

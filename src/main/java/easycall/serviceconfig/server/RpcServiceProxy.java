package easycall.serviceconfig.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 一个RPC服务实例的代理类
 * @author 翁富鑫 2019/3/26 14:15
 */
public class RpcServiceProxy<T> implements InvocationHandler {

    private T rpcServiceObject;

    public RpcServiceProxy(T t) {
        this.rpcServiceObject = t;
    }

    public T getProxyInstance() {
        return (T)Proxy.newProxyInstance(rpcServiceObject.getClass().getClassLoader() , rpcServiceObject.getClass().getInterfaces() , this);
    }

    /**
     *  可以添加额外的Handler对当前的服务调用过程进行监控
     *  比如监控调用的时间等等
     */

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(rpcServiceObject , args);
    }
}

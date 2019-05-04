package wfx.consumer;

import easycall.serviceconfig.client.DefaultRpcConsumerProxyContainer;
import easycall.serviceconfig.client.RpcConsumerProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 封装RpcConsumerProxy对象，在Spring的生命周期中对RpcConsumer进行操作
 * TODO 在这之前需要将ClientBoot中的所有实例全部生成Bean，然后才能在Spring中拿到配置
 * TODO 整个操作就相当于将ClientBoot中的生成逻辑重新在Spring中写一遍
 * @author 翁富鑫 2019/4/29 22:40
 */
public class ConsumerSpringBean implements FactoryBean, InitializingBean{

    private DefaultRpcConsumerProxyContainer consumerProxyContainer;
    private Class<?> interfaceClass;
    private Map<String,Object> paramMap;

    @Nullable
    @Override
    public Object getObject() throws Exception {
        // 获取RPC代理对象
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, this.consumerProxyContainer.getProxyByInterfaceType(interfaceClass, paramMap));
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("ConsumerSpringBean --- 初始化完成");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

package wfx.consumer;

import easycall.registercenter.client.Subscriber;
import easycall.serviceconfig.client.DefaultRpcConsumerProxyContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 封装RpcConsumerProxy对象，在Spring的生命周期中对RpcConsumer进行操作
 * @author 翁富鑫 2019/4/29 22:40
 */
public class ConsumerSpringBean implements FactoryBean, InitializingBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private DefaultRpcConsumerProxyContainer consumerProxyContainer;
    private Class<?> interfaceClass;
    private Map<String,Object> paramMap;
    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        // 获取RPC代理对象
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, this.consumerProxyContainer.getProxyByInterfaceType(interfaceClass, paramMap));
    }

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

    public DefaultRpcConsumerProxyContainer getConsumerProxyContainer() {
        return consumerProxyContainer;
    }

    public void setConsumerProxyContainer(DefaultRpcConsumerProxyContainer consumerProxyContainer) {
        this.consumerProxyContainer = consumerProxyContainer;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 启动订阅端
        ((Subscriber)applicationContext.getBean("subscriber")).start();
    }
}

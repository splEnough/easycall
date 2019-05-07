package wfx.provider;

import easycall.serviceconfig.server.RPCProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * 构造ProviderSpringBean
 * @author 翁富鑫 2019/5/5 21:43
 */
public class ProviderBeanDefinitionBuilder {

    /**
     * 实现类的BeanName
     */
    private String targetBeanName;
    private Provider annotation;
    private String proxyBeanName;

    public ProviderBeanDefinitionBuilder(String targetBeanName, Provider annotation, String proxyBeanName) {
        this.targetBeanName = targetBeanName;
        this.annotation = annotation;
        this.proxyBeanName = proxyBeanName;
    }

    public BeanDefinition build(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ProviderSpringBean.class);
        RPCProvider rpcProvider = new RPCProvider();
        String serviceName = annotation.interfaceClass().getTypeName();
        rpcProvider.setServiceName(serviceName);
        rpcProvider.setVersion(annotation.version());
        // 处理Bean参数
        beanDefinitionBuilder.addPropertyValue("targetBeanName" , targetBeanName);
        beanDefinitionBuilder.addPropertyValue("rpcProvider", rpcProvider);
        beanDefinitionBuilder.addPropertyValue("version" , annotation.version());
        beanDefinitionBuilder.addPropertyValue("threadPoolSize" , annotation.threadPoolSize());
        beanDefinitionBuilder.addPropertyValue("proxyBeanName" , proxyBeanName);
        return beanDefinitionBuilder.getBeanDefinition();
    }

}

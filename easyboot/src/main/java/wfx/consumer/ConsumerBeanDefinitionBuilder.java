package wfx.consumer;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * 构造Consumer注解的RPC接口代理Bean
 * @author 翁富鑫 2019/4/29 22:34
 */
public class ConsumerBeanDefinitionBuilder {

    private BeanFactory beanFactory;
    private final Class<?> interfaceClass;
    private Consumer annotation;
    private ConsumerProperties consumerProperties;

    public ConsumerBeanDefinitionBuilder(BeanFactory beanFactory , Class<?> interfaceClass, Consumer annotation, ConsumerProperties consumerProperties) {
        this.beanFactory = beanFactory;
        this.interfaceClass = interfaceClass;
        this.annotation = annotation;
        this.consumerProperties = consumerProperties;
    }

    public BeanDefinition build() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ConsumerSpringBean.class);
        String serviceName = interfaceClass.getTypeName();
        String version = annotation.version();
        long timeout = annotation.timeout();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("serviceName" , serviceName);
        paramMap.put("version" , version);
        paramMap.put("timeout" , timeout);
        beanDefinitionBuilder.addPropertyValue("paramMap" , paramMap);
        beanDefinitionBuilder.addPropertyValue("interfaceClass", interfaceClass);
        beanDefinitionBuilder.addPropertyReference("consumerProxyContainer", "consumerProxyContainer");
        return beanDefinitionBuilder.getBeanDefinition();
    }

}

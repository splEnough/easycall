package wfx.consumer;

import easycall.initconfig.ClientInitializer;
import easycall.network.client.ConnectionFactory;
import easycall.network.client.management.ConnectionManager;
import easycall.registercenter.client.Subscriber;
import easycall.serviceconfig.client.RpcConsumerProxyContainer;
import easycall.serviceconfig.client.RpcMessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import wfx.config.Consumer;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 翁富鑫 2019/4/29 22:21
 */
public class ConsumerParserPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, BeanClassLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerParserPostProcessor.class);

    private ClassLoader classLoader;

    private ApplicationContext context;

    private Map<String, BeanDefinition> beanDefinitions = new LinkedHashMap<String, BeanDefinition>();

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        postProcessBeanFactory(beanFactory, (BeanDefinitionRegistry) beanFactory);
    }

    private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry registry) {
        ConsumerProperties consumerProperties = bindConsumerProperties(beanFactory);
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);

            String beanClassName = definition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(definition.getBeanClassName(), this.classLoader);
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        ConsumerParserPostProcessor.this.parseElement(field, consumerProperties);
                    }
                });
            }
        }


        for (String beanName : beanDefinitions.keySet()) {
            if (context.containsBean(beanName)) {
                throw new IllegalArgumentException("[HSF Starter] Spring context already has a bean named " + beanName
                        + ", please change @HSFConsumer field name.");
            }
            registry.registerBeanDefinition(beanName, beanDefinitions.get(beanName));
            logger.info("registered HSFConsumerBean \"{}\" in spring context.", beanName);
        }
    }

    private ConsumerProperties bindConsumerProperties(ConfigurableListableBeanFactory beanFactory) {
        ConsumerProperties consumerProperties = new ConsumerProperties();
        consumerProperties.setSubscriber((Subscriber) beanFactory.getBean("subscriber"));
        consumerProperties.setClientInitializer((ClientInitializer) beanFactory.getBean("clientInitializer"));
        consumerProperties.setConnectionFactory((ConnectionFactory) beanFactory.getBean("connectionManager"));
        consumerProperties.setRpcMessageManager((RpcMessageManager) beanFactory.getBean("rpcMessageManager"));
        consumerProperties.setConsumerProxyContainer((RpcConsumerProxyContainer)beanFactory.getBean("rpcConsumerProxyContainer"));
        consumerProperties.setConnectionManager((ConnectionManager) beanFactory.getBean("connectionManager"));
        return consumerProperties;
    }

    private void parseElement(Field field, ConsumerProperties properties) {
        Consumer annotation = AnnotationUtils.getAnnotation(field, Consumer.class);
        if (annotation == null) {
            return;
        }

        ConsumerBeanDefinitionBuilder beanDefinitionBuilder = new ConsumerBeanDefinitionBuilder(beanFactory, field.getType(),
                annotation, properties);
        BeanDefinition beanDefinition = beanDefinitionBuilder.build();
        beanDefinitions.put(field.getName(), beanDefinition);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
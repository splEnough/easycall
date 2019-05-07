package wfx.consumer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * @author 翁富鑫 2019/4/29 22:21
 */
public class ConsumerParserPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, BeanClassLoaderAware, Ordered {

//    private static final Logger logger = LoggerFactory.getLogger(ConsumerParserPostProcessor.class);

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
//            logger.info("registered HSFConsumerBean \"{}\" in spring context.", beanName);
        }
    }

    private ConsumerProperties bindConsumerProperties(ConfigurableListableBeanFactory beanFactory) {
        ConsumerProperties consumerProperties = new ConsumerProperties();
        consumerProperties.setSubscriber(beanFactory.getBeanDefinition("subscriber"));
        consumerProperties.setClientInitializer(beanFactory.getBeanDefinition("clientParam"));
        consumerProperties.setConnectionFactory(beanFactory.getBeanDefinition("connectionFactory"));
        consumerProperties.setRpcMessageManager(beanFactory.getBeanDefinition("rpcMessageManager"));
        consumerProperties.setConsumerProxyContainer(beanFactory.getBeanDefinition("rpcConsumerProxyContainer"));
        consumerProperties.setConnectionManager( beanFactory.getBeanDefinition("connectionManager"));
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

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
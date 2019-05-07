package wfx.provider;

import easycall.Util.StringUtil;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author 翁富鑫 2019/4/29 21:49
 */
public class ProviderServiceBeanGeneratorSelector implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {

    private static final String BEAN_NAME_SUFFIX = "ProviderBean";

    private ClassLoader classLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String className = importingClassMetadata.getClassName();
        String targetClassBeanName = StringUtil.firstCharToLow(className.substring(className.lastIndexOf(".") + 1));
        String proxyBeanName = className + BEAN_NAME_SUFFIX;
        Class<?> targetClass;
        try {
            targetClass = this.classLoader.loadClass(className);
            Provider provider = AnnotationUtils.findAnnotation(targetClass, Provider.class);
            ProviderBeanDefinitionBuilder beanDefinitionBuilder = new ProviderBeanDefinitionBuilder(targetClassBeanName, provider, proxyBeanName);
            BeanDefinition beanDefinition = beanDefinitionBuilder.build(registry);
            if (beanDefinition != null) {
                if (!registry.containsBeanDefinition(proxyBeanName)) {
                    registry.registerBeanDefinition(proxyBeanName, beanDefinition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}

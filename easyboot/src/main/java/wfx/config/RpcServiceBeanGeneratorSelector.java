package wfx.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author 翁富鑫 2019/4/29 21:49
 */
public class RpcServiceBeanGeneratorSelector implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String className = importingClassMetadata.getClassName();
        System.out.println("className:" + className);
        System.out.println("detail:" + importingClassMetadata.toString());
    }
}

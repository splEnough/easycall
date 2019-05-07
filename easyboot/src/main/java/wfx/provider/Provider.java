package wfx.provider;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 翁富鑫 2019/5/4 23:39
 */
@Component
@Target(ElementType.TYPE)
@Import(ProviderServiceBeanGeneratorSelector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {
    /**
     * 对应的服务接口
     */
    Class<?> interfaceClass();

    String version() default "1.0";

    /**
     * 私有线程池的线程熟量，默认-1则表示不分配线程池
     */
    int threadPoolSize() default -1;
}

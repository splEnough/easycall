package wfx.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import wfx.provider.ProviderServiceBeanGeneratorSelector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将直接的类声明为RPC服务调用端
 * @author 翁富鑫 2019/4/29 21:43
 */
@Target(ElementType.FIELD)
@Bean
@Import(ProviderServiceBeanGeneratorSelector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {

    /**
     * 作为注解的beanName
     */
    String value();

    /**
     * RPC服务的接口类型
     */
    Class<?> interfaceName();

    /**
     * RPC服务的版本
     */
    String version();

    /**
     * 接口调用的超时时间，单位为毫秒
     */
    long timeout();
}

package wfx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 将直接的类声明为RPC服务调用端
 * @author 翁富鑫 2019/4/29 21:43
 */
@Target(ElementType.FIELD)
@Bean
@Import(RpcServiceBeanGeneratorSelector.class)
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

package easycall.serviceconfig.server.declaration;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * 注解在一个ServiceImpl上表示当前的Bean为一个RPC服务
 * TODO 迁移到Boot-AutoConfigure
 * @author 翁富鑫 2019/3/24 14:25
 */
@Component
@Import(RpcServiceRegister.class)
@Deprecated
public @interface RpcService {

    /**
     * 当前RPC服务所对应的版本
     */
    String version() default "1.0.0";

    // TODO 单独的线程池划分结构
}

package wfx.provider;

import easycall.initconfig.ServerInitializer;
import easycall.network.server.starter.DefaultNioServerStarter;
import easycall.network.server.starter.ServerStarter;
import easycall.registercenter.server.DefaultZookeeperRegister;
import easycall.registercenter.server.Register;
import easycall.serviceconfig.server.RpcProviderManager;
import easycall.thread.DefaultExecutorManager;
import easycall.thread.ExecutorManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provider端自动配置项
 * @author 翁富鑫 2019/5/5 21:16
 */
@Configuration
@EnableConfigurationProperties(ProviderConfigProperties.class)
public class ProviderConfigAutoConfig {

    @Bean
    public ExecutorManager executorManager() {
        return new DefaultExecutorManager();
    }

    @Bean
    public ServerInitializer serverInitializer() {
        return new ServerInitializer();
    }

    @Bean
    public Register register(ProviderConfigProperties providerConfigProperties) {
        return new DefaultZookeeperRegister(providerConfigProperties.getConnString());
    }

    @Bean
    public RpcProviderManager rpcProviderManager(Register register) {
        return new RpcProviderManager(register);
    }

    @Bean
    public ServerStarter serverStarter(ServerInitializer initializer, RpcProviderManager rpcProviderManager) {
        return new DefaultNioServerStarter(initializer, executorManager(), rpcProviderManager);
    }

}

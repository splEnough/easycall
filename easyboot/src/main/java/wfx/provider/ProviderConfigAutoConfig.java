package wfx.provider;

import easycall.Util.StringUtil;
import easycall.initconfig.ServerInitializer;
import easycall.network.server.starter.DefaultNioServerStarter;
import easycall.network.server.starter.ServerStarter;
import easycall.registercenter.server.DefaultZookeeperRegister;
import easycall.registercenter.server.Register;
import easycall.serviceconfig.server.RpcProviderManager;
import easycall.thread.DefaultExecutorManager;
import easycall.thread.ExecutorManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provider端自动配置项
 * @author 翁富鑫 2019/5/5 21:16
 */
@Configuration
@EnableConfigurationProperties(ProviderConfigProperties.class)
@ConditionalOnProperty(prefix = "easycall.provider", name = "enabled", havingValue = "true")
public class ProviderConfigAutoConfig {

    @Bean
    public ExecutorManager executorManager() {
        return new DefaultExecutorManager();
    }

    @Bean
    public ServerInitializer serverInitializer(ProviderConfigProperties providerConfigProperties) {
        ServerInitializer serverInitializer = new ServerInitializer();
        if (!StringUtil.isEmpty(providerConfigProperties.getPort())) {
            serverInitializer.setPort(Integer.parseInt(providerConfigProperties.getPort()));
        }
        if (!StringUtil.isEmpty(providerConfigProperties.getConnString())) {
            serverInitializer.setConnString(providerConfigProperties.getConnString());
        }
        if (!StringUtil.isEmpty(providerConfigProperties.getSerialize())) {
            serverInitializer.setSerializeType(providerConfigProperties.getSerialize());
        }
        return serverInitializer;
    }

    @Bean
    public Register register(ServerInitializer serverInitializer) {
        return new DefaultZookeeperRegister(serverInitializer.getConnString());
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

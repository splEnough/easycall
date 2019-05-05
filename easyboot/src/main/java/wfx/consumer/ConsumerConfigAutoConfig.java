package wfx.consumer;

import easycall.initconfig.ClientInitializer;
import easycall.loadbalance.LoadBalanceType;
import easycall.network.client.ConnectionFactory;
import easycall.network.client.connection.PooledConnectionFactory;
import easycall.network.client.management.ConnectionManager;
import easycall.network.client.management.DefaultConnectionManager;
import easycall.registercenter.client.DefaultZookeeperSubscriber;
import easycall.registercenter.client.Subscriber;
import easycall.serviceconfig.client.DefaultRpcConsumerProxyContainer;
import easycall.serviceconfig.client.DefaultRpcMessageManager;
import easycall.serviceconfig.client.RpcConsumerProxyContainer;
import easycall.serviceconfig.client.RpcMessageManager;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Consumer端自动配置项
 * @author 翁富鑫 2019/4/29 22:12
 */
@Configuration
@ConditionalOnProperty(name = "enabled" , prefix = "easycall.consumer", havingValue = "true")
@EnableConfigurationProperties(ConsumerConfigProperties.class)
public class ConsumerConfigAutoConfig {

    @Bean
    public static BeanFactoryPostProcessor consumerParserPostProcessor() {
        return new ConsumerParserPostProcessor();
    }

//    @Bean
//    public static BeanFactoryPostProcessor simple() {
//        return new SimpleProcessor();
//    }

    public ConsumerConfigAutoConfig() {
        System.out.println("consumerConfigAutoConfig --- init()");
    }

    @Bean
    public ClientInitializer clientInitializer() {
        return new ClientInitializer();
    }

    @Bean
    public ConnectionManager connectionManager() {
        return new DefaultConnectionManager();
    }

    @Bean
    public Subscriber subscriber(ConsumerConfigProperties consumerConfigProperties) {
        System.out.println("connString:" + consumerConfigProperties.getConnString());
        return new DefaultZookeeperSubscriber(consumerConfigProperties.getConnString());
    }

    @Bean
    public RpcMessageManager rpcMessageManager() {
        return new DefaultRpcMessageManager();
    }

    @Bean
    public ConnectionFactory connectionFactory(ConsumerConfigProperties consumerConfigProperties) {
        return new PooledConnectionFactory(connectionManager(), LoadBalanceType.getLoadBalancerByCode(
                ((LoadBalanceType)clientInitializer().getInitialParam("loadBalanceType")).getCode(), subscriber(consumerConfigProperties)),rpcMessageManager()
        );
    }

    @Bean
    public RpcConsumerProxyContainer rpcConsumerProxyContainer(ConsumerConfigProperties consumerConfigProperties) {
        return new DefaultRpcConsumerProxyContainer(clientInitializer(), connectionFactory(consumerConfigProperties), rpcMessageManager());
    }

}

package wfx.consumer;

import easycall.Util.StringUtil;
import easycall.initconfig.ClientParam;
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
    public ClientParam clientParam(ConsumerConfigProperties consumerConfigProperties) {
        ClientParam clientParam = new ClientParam();
        if (!StringUtil.isEmpty(consumerConfigProperties.getConnString())) {
            clientParam.setConnString(consumerConfigProperties.getConnString());
        }
        if (!StringUtil.isEmpty(consumerConfigProperties.getPort())) {
            clientParam.setPort(Integer.parseInt(consumerConfigProperties.getPort()));
        }
        if (!StringUtil.isEmpty(consumerConfigProperties.getSerializeType())) {
            clientParam.setSerializeType(consumerConfigProperties.getSerializeType());
        }
        if (!StringUtil.isEmpty(consumerConfigProperties.getVersion())) {
            clientParam.setVersion(consumerConfigProperties.getVersion());
        }
        if (!StringUtil.isEmpty(consumerConfigProperties.getLoadBalanceType())) {
            clientParam.setLoadBalanceType(Integer.parseInt(consumerConfigProperties.getLoadBalanceType()));
        }
        if (!StringUtil.isEmpty(consumerConfigProperties.getRpcTimeout())) {

        }
        return clientParam;
    }

    @Bean
    public ConnectionManager connectionManager() {
        return new DefaultConnectionManager();
    }

    @Bean
    public Subscriber subscriber(ClientParam clientParam) {
        return new DefaultZookeeperSubscriber(clientParam.getConnString());
    }

    @Bean
    public RpcMessageManager rpcMessageManager() {
        return new DefaultRpcMessageManager();
    }

    @Bean
    public ConnectionFactory connectionFactory(ClientParam clientParam) {
        return new PooledConnectionFactory(connectionManager(), LoadBalanceType.getLoadBalancerByCode(
                clientParam.getLoadBalanceType().getCode(), subscriber(clientParam)),rpcMessageManager()
        );
    }

    @Bean
    public RpcConsumerProxyContainer rpcConsumerProxyContainer(ConsumerConfigProperties consumerConfigProperties) {
        return new DefaultRpcConsumerProxyContainer(clientParam(consumerConfigProperties), connectionFactory(clientParam(consumerConfigProperties)), rpcMessageManager());
    }

}

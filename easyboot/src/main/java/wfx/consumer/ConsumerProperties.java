package wfx.consumer;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * 初始化RpcConsumerProxy需要的参数
 * @author 翁富鑫 2019/5/4 9:33
 */
public class ConsumerProperties {
    /**
     * 目标服务名
     */
    private String targetService;
    /**
     * 目标方法名
     */
    private String targetMethod;
    /**
     * 目标服务版本
     */
    private String targetVersion;

    /**
     * 超时时间，毫秒
     */
    private long timeout;

    private BeanDefinition connectionManager;
    private BeanDefinition connectionFactory;
    private BeanDefinition subscriber;
    private BeanDefinition clientInitializer;
    private BeanDefinition consumerProxyContainer;
    private BeanDefinition rpcMessageManager;

    public String getTargetService() {
        return targetService;
    }

    public void setTargetService(String targetService) {
        this.targetService = targetService;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public BeanDefinition getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(BeanDefinition connectionManager) {
        this.connectionManager = connectionManager;
    }

    public BeanDefinition getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(BeanDefinition connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public BeanDefinition getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(BeanDefinition subscriber) {
        this.subscriber = subscriber;
    }

    public BeanDefinition getClientInitializer() {
        return clientInitializer;
    }

    public void setClientInitializer(BeanDefinition clientInitializer) {
        this.clientInitializer = clientInitializer;
    }

    public BeanDefinition getConsumerProxyContainer() {
        return consumerProxyContainer;
    }

    public void setConsumerProxyContainer(BeanDefinition consumerProxyContainer) {
        this.consumerProxyContainer = consumerProxyContainer;
    }

    public BeanDefinition getRpcMessageManager() {
        return rpcMessageManager;
    }

    public void setRpcMessageManager(BeanDefinition rpcMessageManager) {
        this.rpcMessageManager = rpcMessageManager;
    }
}

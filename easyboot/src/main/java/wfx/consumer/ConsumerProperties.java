package wfx.consumer;

import easycall.initconfig.ClientInitializer;
import easycall.network.client.ConnectionFactory;
import easycall.network.client.management.ConnectionManager;
import easycall.registercenter.client.Subscriber;
import easycall.serviceconfig.client.RpcConsumerProxyContainer;
import easycall.serviceconfig.client.RpcMessageManager;

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

    private ConnectionManager connectionManager;
    private ConnectionFactory connectionFactory;
    private Subscriber subscriber;
    private ClientInitializer clientInitializer;
    private RpcConsumerProxyContainer consumerProxyContainer;
    private RpcMessageManager rpcMessageManager;

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

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public ClientInitializer getClientInitializer() {
        return clientInitializer;
    }

    public void setClientInitializer(ClientInitializer clientInitializer) {
        this.clientInitializer = clientInitializer;
    }

    public RpcConsumerProxyContainer getConsumerProxyContainer() {
        return consumerProxyContainer;
    }

    public void setConsumerProxyContainer(RpcConsumerProxyContainer consumerProxyContainer) {
        this.consumerProxyContainer = consumerProxyContainer;
    }

    public RpcMessageManager getRpcMessageManager() {
        return rpcMessageManager;
    }

    public void setRpcMessageManager(RpcMessageManager rpcMessageManager) {
        this.rpcMessageManager = rpcMessageManager;
    }
}

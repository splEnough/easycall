package easycall.boot;

import easycall.initconfig.ClientInitializer;
import easycall.loadbalance.LoadBalanceType;
import easycall.network.client.connection.PooledConnectionFactory;
import easycall.network.client.ConnectionFactory;
import easycall.network.client.management.ConnectionManager;
import easycall.network.client.management.DefaultConnectionManager;
import easycall.registercenter.client.DefaultZookeeperSubscriber;
import easycall.registercenter.client.Subscriber;
import easycall.serviceconfig.client.*;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * 客户端启动器
 * @author 翁富鑫 2019/3/28 16:22
 */
public class ClientBoot implements Closeable{
    private ConnectionManager connectionManager;
    private ConnectionFactory connectionFactory;
    private Subscriber subscriber;
    private ClientInitializer clientInitializer;
    private String connString;
    private RpcConsumerProxyContainer consumerProxyContainer;
    private RpcMessageManager rpcMessageManager;

    public ClientBoot(String connString) {
        this.connString = connString;
        this.clientInitializer = new ClientInitializer();
        this.connectionManager = new DefaultConnectionManager();
        this.subscriber = new DefaultZookeeperSubscriber(this.connString);
        this.rpcMessageManager = new DefaultRpcMessageManager();
        this.connectionFactory = new PooledConnectionFactory(connectionManager, LoadBalanceType.getLoadBalancerByCode(
                ((LoadBalanceType)clientInitializer.getInitialParam("loadBalanceType")).getCode(), subscriber),rpcMessageManager
        );
        consumerProxyContainer = new DefaultRpcConsumerProxyContainer(clientInitializer, connectionFactory, rpcMessageManager);
    }

    public void start() {
        this.subscriber.start();
    }

    @Override
    public void close() throws IOException {
        this.connectionFactory.close();
        this.connectionManager.close();
        this.subscriber.close();
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * 订阅一个服务，返回当前接口的代理实现类实例
     * @param interfaceClass 所订阅的服务对应的接口类型
     * @return 代理实现对象
     */
    public Object subscribeService(Class<?> interfaceClass) {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, this.consumerProxyContainer.getProxyByInterfaceType(interfaceClass));
    }
}

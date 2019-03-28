package easycall.network.client;

import easycall.config.ClientInitializer;
import easycall.loadbalance.LoadBalanceType;
import easycall.network.client.connection.PooledConnectionFactory;
import easycall.network.common.connection.ConnectionFactory;
import easycall.network.common.connection.management.ConnectionManager;
import easycall.network.common.connection.management.DefaultConnectionManager;
import easycall.registercenter.DefaultZookeeperRegisterCenterClient;
import easycall.registercenter.RegisterCenterClient;
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
    private RegisterCenterClient registerCenterClient;
    private ClientInitializer clientInitializer;
    private String connString;
    private RpcConsumerProxyContainer consumerProxyContainer;
    private RpcMessageManager rpcMessageManager;

    public ClientBoot(String connString) {
        this.connString = connString;
        this.clientInitializer = new ClientInitializer();
        this.connectionManager = new DefaultConnectionManager();
        this.registerCenterClient = new DefaultZookeeperRegisterCenterClient(this.connString);
        this.rpcMessageManager = new DefaultRpcMessageManager();
        this.connectionFactory = new PooledConnectionFactory(connectionManager, LoadBalanceType.getLoadBalancerByCode(
                ((LoadBalanceType)clientInitializer.getInitialParam("loadBalanceType")).getCode(), registerCenterClient),rpcMessageManager
        );
        consumerProxyContainer = new DefaultRpcConsumerProxyContainer(clientInitializer, connectionFactory, rpcMessageManager);
    }

    public void start() {
        this.registerCenterClient.start();
    }

    @Override
    public void close() throws IOException {
        this.connectionFactory.close();
        this.connectionManager.close();
        this.registerCenterClient.close();
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public RegisterCenterClient getRegisterCenterClient() {
        return registerCenterClient;
    }

    /**
     * 订阅一个服务，返回当前接口的代理实现类实例
     * @param interfaceClass 所订阅的服务对应的接口类型
     * @return 代理实现对象
     */
    public Object exportService(Class<?> interfaceClass) {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, this.consumerProxyContainer.getProxyByInterfaceType(interfaceClass));
    }
}

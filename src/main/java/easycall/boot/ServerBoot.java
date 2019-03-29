package easycall.boot;

import easycall.initconfig.ServerInitializer;
import easycall.network.server.starter.DefaultNioServerStarter;
import easycall.network.server.starter.ServerStarter;
import easycall.registercenter.DefaultZookeeperRegisterCenterClient;
import easycall.registercenter.RegisterCenterClient;
import easycall.serviceconfig.server.RpcProviderManager;
import easycall.thread.DefaultExecutorManager;
import easycall.thread.ExecutorManager;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务端启动类
 * @author 翁富鑫 2019/3/26 9:47
 */
public class ServerBoot implements Closeable{

    private ServerInitializer initializer ;
    private ExecutorManager executorManager;
    ServerStarter serverStarter;
    private RegisterCenterClient registerCenterClient;
    /**
     * 服务端只能启动一次
     */
    private AtomicBoolean isStart = new AtomicBoolean(false);
    /**
     * zookeeper连接字符串
     */
    private String connString;
    /**
     * 服务提供者管理器
     */
    private RpcProviderManager rpcProviderManager;

    public ServerBoot(String connString) {
        this.connString = connString;
        init();
    }

    private void init() {
        initializer = new ServerInitializer();
        executorManager = new DefaultExecutorManager();
        registerCenterClient = new DefaultZookeeperRegisterCenterClient(connString);
        rpcProviderManager = new RpcProviderManager(registerCenterClient);
        serverStarter = new DefaultNioServerStarter(initializer, executorManager, rpcProviderManager);
    }

    /**
     * 启动RPC服务提供端
     */
    public void start() {
        if (isStart.compareAndSet(false , true)) {
            try {
                serverStarter.start();
                registerCenterClient.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        serverStarter.close();
        registerCenterClient.close();
        executorManager.close();
    }

    public ServerInitializer getInitializer() {
        return initializer;
    }

    public ExecutorManager getExecutorManager() {
        return executorManager;
    }

    public ServerStarter getServerStarter() {
        return serverStarter;
    }

    public RegisterCenterClient getRegisterCenterClient() {
        return registerCenterClient;
    }

    public AtomicBoolean getIsStart() {
        return isStart;
    }

    public String getConnString() {
        return connString;
    }

    public RpcProviderManager getRpcProviderManager() {
        return rpcProviderManager;
    }
}

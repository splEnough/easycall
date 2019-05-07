package easycall.boot;

import easycall.initconfig.ServerParam;
import easycall.network.server.starter.DefaultNioServerStarter;
import easycall.network.server.starter.ServerStarter;
import easycall.registercenter.server.DefaultZookeeperRegister;
import easycall.registercenter.server.Register;
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

    private ServerParam initializer ;
    private ExecutorManager executorManager;
    ServerStarter serverStarter;
    private Register register;
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
        initializer = new ServerParam();
        executorManager = new DefaultExecutorManager();
        register = new DefaultZookeeperRegister(connString);
        rpcProviderManager = new RpcProviderManager(register);
        serverStarter = new DefaultNioServerStarter(initializer, executorManager, rpcProviderManager);
    }

    /**
     * 启动RPC服务提供端
     */
    public void start() {
        if (isStart.compareAndSet(false , true)) {
            try {
                serverStarter.start();
                register.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        serverStarter.close();
        register.close();
        executorManager.close();
    }

    public ServerParam getInitializer() {
        return initializer;
    }

    public ExecutorManager getExecutorManager() {
        return executorManager;
    }

    public ServerStarter getServerStarter() {
        return serverStarter;
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

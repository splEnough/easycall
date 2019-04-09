package easycall.registercenter.server;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.curator.framework.imps.CuratorFrameworkState.STARTED;

/**
 * @author 翁富鑫 2019/4/9 16:28
 */
public class DefaultZookeeperRegister implements Register {

    private static Logger logger = LoggerFactory.getLogger(DefaultZookeeperRegister.class);

    /**
     * zookeeper
     */
    private String zookeeperConnectionString;

    /**
     * zookeeper连接
     */
    private CuratorFramework zkClient;

    /**
     * 所有的业务节点的父节点
     */
    private static final String SERVICE_PARENT_PATH = "services";

    /**
     * 连接重试时间为3秒
     */
    private static final int RETRY_INTERVAL_MS = 3000;

    /**
     * 已经注册了的服务的路径
     */
    private Set<String> registeredServicesPathSet = Collections.synchronizedSet(new HashSet<String>());

    /**
     * 重新连接之后用于注册所有现有服务的线程池
     */
    private ExecutorService reConnectExecutor = Executors.newSingleThreadExecutor();

    private volatile boolean isRunning = false;

    public DefaultZookeeperRegister(String zookeeperConnectionString) {
        this.zookeeperConnectionString = zookeeperConnectionString;
    }

    @Override
    public void start() {
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperConnectionString)
                .namespace(SERVICE_PARENT_PATH)
                .retryPolicy(new RegisterServiceRetryPolicy(RETRY_INTERVAL_MS))
                .build();
        zkClient.start();
    }

    @Override
    public boolean registerService(String serviceName, String version, String ip) {
        String ipNodePath = "/" + serviceName + "/" + version + "/" + ip;
        try {
            Stat stat = zkClient.checkExists().creatingParentsIfNeeded().forPath(ipNodePath);
            if (stat == null) {
                // 还没有创建节点，则创建临时节点
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ipNodePath);
            }
            registeredServicesPathSet.add(ipNodePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean registerService(String serviceName, String version) {
        try {
            String ip = getCurrentHostIp();
            return registerService(serviceName, version, ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unRegisterService(String serviceName, String version) {
        String ip = null;
        try {
            ip = getCurrentHostIp();
            String currentPath = "/" + serviceName + "/" + version + "/" + ip;
            this.zkClient.delete().guaranteed().forPath(currentPath);
            registeredServicesPathSet.remove(currentPath);
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        this.zkClient.close();
        registeredServicesPathSet.clear();
    }

    private String getCurrentHostIp() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        return ip;
    }

    class RegistAllServices implements ConnectionStateListener {

        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            if (newState == ConnectionState.RECONNECTED) {
                // 注册所有的服务
            }
        }
    }

    private class RegisterServiceRetryPolicy implements RetryPolicy {

        private final int retryIntervalMs;

        // 开启一个线程执行重新发布服务的任务

        public RegisterServiceRetryPolicy(int retryIntervalMs) {
            checkArgument(retryIntervalMs > 0);
            this.retryIntervalMs = retryIntervalMs;
        }

        @Override
        public boolean allowRetry(int retryCount, long elapsedTimeMs, RetrySleeper sleeper) {
            try {
                sleeper.sleepFor(retryIntervalMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            System.out.println("true");
            if (!isRunning) {
                reConnectExecutor.submit(new RegistAllServiceTask());
            }
            return true;
        }

    }

    private class RegistAllServiceTask implements Runnable {

        @Override
        public void run() {
            isRunning = true;
            while (zkClient.getState() != STARTED) {
                // 客户端还没有启动
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 已经启动，开始重新连接
            try {
                for (String servicePath : registeredServicesPathSet) {
                    Stat stat = zkClient.checkExists().creatingParentsIfNeeded().forPath(servicePath);
                    if (stat == null) {
                        // 还没有创建节点，则创建临时节点
                        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(servicePath);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Republish server services failed!");
            }
            System.out.println("一个runnable完毕");
            isRunning = false;
        }
    }

}

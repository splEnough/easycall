package easycall.registercenter.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * 循环执行zookeeper操作，这样才能在断开连接的时候触发重连机制
     */
    private ExecutorService heartBeatExecutor = Executors.newSingleThreadExecutor();

    public DefaultZookeeperRegister(String zookeeperConnectionString) {
        this.zookeeperConnectionString = zookeeperConnectionString;
    }

    AtomicBoolean started = new AtomicBoolean();

    private static String hostIp;

    static {
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        hostIp = ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            zkClient = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperConnectionString)
                    .namespace(SERVICE_PARENT_PATH)
                    .retryPolicy(new RetryForever(RETRY_INTERVAL_MS))
                    .build();
            zkClient.getConnectionStateListenable().addListener(new RegistAllServices());
            zkClient.start();
            heartBeatExecutor.submit(() -> {
                while (true) {
                    TimeUnit.SECONDS.sleep(5);
                    try {
                        zkClient.getData().forPath("/");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public boolean registerService(String serviceName, String version, String ip) {
        System.out.println("DefaultZookeeperRegister --- registerService:serviceName" + serviceName + ",version:" + version + ",ip:" + ip);
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
        heartBeatExecutor.shutdown();    }

    private static String getHostIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String getCurrentHostIp() throws UnknownHostException {

//        InetAddress address = InetAddress.getLocalHost();
//        String ip = address.getHostAddress();
//        return ip;
        return hostIp;
    }

    class RegistAllServices implements ConnectionStateListener {

        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            System.out.println("state:" + newState);
            if (newState == ConnectionState.RECONNECTED) {
                // 注册所有的服务
                try {
                    System.out.println("重新注册服务");
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
            }
        }
    }

}

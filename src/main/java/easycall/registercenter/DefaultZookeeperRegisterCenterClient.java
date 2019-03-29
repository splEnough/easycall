package easycall.registercenter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 默认的Zookeeper注册中心客户端
 * @author 翁富鑫 2019/3/24 16:51
 */
public class DefaultZookeeperRegisterCenterClient implements RegisterCenterClient {

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
     * 每个业务-版本对应的提供方服务器ip列表，key格式为：/+serviceconfig+/+version
     */
    private Map<String, List<String>> serviceVersionIpsListMap = new HashMap<>();

    /**
     * 正在监听子节点数据变化的节点路径
     */
    private Set<String> listeningSubPathSet = Collections.synchronizedSet(new HashSet<>());

    /**
     * 执行子节点数据变更事件监听
     */
    private ExecutorService subNodeChangedExecutorService = Executors.newFixedThreadPool(10);

    public DefaultZookeeperRegisterCenterClient(String zookeeperConnectionString) {
        this.zookeeperConnectionString = zookeeperConnectionString;
    }

    /**
     * 启动客户端
     */
    public void start() {
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperConnectionString)
                .namespace(SERVICE_PARENT_PATH)
                .retryPolicy(new RetryForever(RETRY_INTERVAL_MS))
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
            return registerService(serviceName , version , ip);
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
            String currentPath = "/" + serviceName + "/" +  version + "/" + ip;
            this.zkClient.delete().guaranteed().forPath(currentPath);
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> subscribeService(String serviceName, String version) {
        String serviceVersionPath = "/" + serviceName + "/" + version;
        try {
            // 直接从Map中获取
            List<String> currentIpList = serviceVersionIpsListMap.get(serviceVersionPath);
            if (currentIpList == null) {
                if (listeningSubPathSet.contains(serviceVersionPath)) {
                    // 正在监听子节点数据变化
                    return null;
                }
                synchronized (serviceVersionPath) {
                    List<String> doubleCheckIpList = serviceVersionIpsListMap.get(serviceVersionPath);
                    if (doubleCheckIpList != null) {
                        // DCL，双重检查
                        return doubleCheckIpList;
                    }
                    // 否则利用zkClient请求数据
                    currentIpList = zkClient.getChildren().forPath(serviceVersionPath);
                    List<String> concurrentList = new CopyOnWriteArrayList<>();
                    concurrentList.addAll(currentIpList);
                    // 数据存入Map
                    serviceVersionIpsListMap.put(serviceVersionPath, concurrentList);
                    // 添加一个子节点监听器
                    PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, serviceVersionPath, true, false, subNodeChangedExecutorService);
                    pathChildrenCache.getListenable().addListener(new ServicePathChildrenCacheListener(serviceVersionPath));
                    pathChildrenCache.start();
                    return currentIpList;
                }
            }
            return currentIpList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        this.zkClient.close();
        this.subNodeChangedExecutorService.shutdown();
    }

    private class ServicePathChildrenCacheListener implements PathChildrenCacheListener {

        private String serviceVersionPath;

        public ServicePathChildrenCacheListener(String serviceVersionPath) {
            this.serviceVersionPath = serviceVersionPath;
        }

        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            List<String> ipList = null;
            switch (event.getType()) {
                case CHILD_ADDED:
                    String addFullPath = event.getData().getPath();
                    String addIp = addFullPath.substring(addFullPath.lastIndexOf("/") + 1);
                    synchronized (serviceVersionPath) {
                        // 锁住当前的操作对象
                        ipList = serviceVersionIpsListMap.get(serviceVersionPath);
                        if (ipList == null) {
                            ipList = new CopyOnWriteArrayList<>();
                        }
                    }
                    ipList.add(addIp);
                    break;
                case INITIALIZED:
                    synchronized (serviceVersionPath) {
                        // 锁住当前的操作对象
                        ipList = serviceVersionIpsListMap.get(serviceVersionPath);
                        if (ipList == null) {
                            ipList = new CopyOnWriteArrayList<>();
                        }
                        List<ChildData> childDatas = event.getInitialData();
                        for (ChildData childData : childDatas) {
                            String singleFullPath = childData.getPath();
                            String singleIp = singleFullPath.substring(singleFullPath.lastIndexOf("/") + 1);
                            ipList.add(singleIp);
                        }
                    }
                    break;
                case CHILD_REMOVED:
                    // 锁住当前的操作对象
                    String removeFullPath = event.getData().getPath();
                    String removeIp = removeFullPath.substring(removeFullPath.lastIndexOf("/") + 1);
                    synchronized (serviceVersionPath) {
                        // 锁住当前的操作对象
                        ipList = serviceVersionIpsListMap.get(serviceVersionPath);
                        if (ipList == null) {
                            ipList = new CopyOnWriteArrayList<>();
                        }
                    }
                    ipList.remove(removeIp);
                    break;
            }
        }
    }

    private String getCurrentHostIp() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        return ip;
    }
}

package easycall.registercenter.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryForever;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 翁富鑫 2019/4/9 16:17
 */
public class DefaultZookeeperSubscriber implements Subscriber {

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
    private Map<String, Set<String>> serviceVersionIpsSetMap = new HashMap<>();

    /**
     * 正在监听子节点数据变化的节点路径
     */
    private Set<String> listeningSubPathSet = Collections.synchronizedSet(new TreeSet<String>());

    /**
     * 执行子节点数据变更事件监听
     */
    private ExecutorService subNodeChangedExecutorService = Executors.newFixedThreadPool(10);

    /**
     * 监听子节点变化的任务
     */
    private List<PathChildrenCache> pathChildrenCaches = new CopyOnWriteArrayList<>();

    private AtomicBoolean started = new AtomicBoolean();

    public DefaultZookeeperSubscriber(String zookeeperConnectionString) {
        this.zookeeperConnectionString = zookeeperConnectionString;
        start();
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            zkClient = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperConnectionString)
                    .namespace(SERVICE_PARENT_PATH)
                    // TODO 重连之后要继续开启监听
                    .retryPolicy(new RetryForever(RETRY_INTERVAL_MS))
                    .build();
            zkClient.start();
        }
    }

    @Override
    public Set<String> subscribeService(String serviceName, String version) {
        String serviceVersionPath = "/" + serviceName + "/" + version;
        System.out.println("DefaultZookeeperSubscriber --- subscribe:" + serviceVersionPath);
        try {
            // 直接从Map中获取
            Set<String> currentIpSet = serviceVersionIpsSetMap.get(serviceVersionPath);
            if (currentIpSet == null) {
                if (listeningSubPathSet.contains(serviceVersionPath)) {
                    // 正在监听子节点数据变化
                    return null;
                }
                synchronized (serviceVersionPath) {
                    Set<String> doubleCheckIpList = serviceVersionIpsSetMap.get(serviceVersionPath);
                    if (doubleCheckIpList != null) {
                        // DCL，双重检查
                        return doubleCheckIpList;
                    }
                    // 否则利用zkClient请求数据
                    List<String> currentIpList = zkClient.getChildren().forPath(serviceVersionPath);
                    Set<String> concurrentSet = Collections.synchronizedSet(new TreeSet<String>());
                    concurrentSet.addAll(currentIpList);
                    // 数据存入Map
                    serviceVersionIpsSetMap.put(serviceVersionPath, concurrentSet);
                    // 添加一个子节点监听器
                    PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, serviceVersionPath, true, false, subNodeChangedExecutorService);
                    pathChildrenCache.getListenable().addListener(new ServicePathChildrenCacheListener(serviceVersionPath));
                    pathChildrenCache.start();
                    pathChildrenCaches.add(pathChildrenCache);
                    return concurrentSet;
                }
            }
            return currentIpSet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        this.zkClient.close();
        this.subNodeChangedExecutorService.shutdown();
        // 停止子节点监听任务
        if (pathChildrenCaches.size() > 0) {
            pathChildrenCaches.forEach((e) -> {
                try {
                    e.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        }
    }

    private class ServicePathChildrenCacheListener implements PathChildrenCacheListener {

        private String serviceVersionPath;

        public ServicePathChildrenCacheListener(String serviceVersionPath) {
            this.serviceVersionPath = serviceVersionPath;
        }

        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            Set<String> ipSet = null;
            switch (event.getType()) {
                case CHILD_ADDED:
                    String addFullPath = event.getData().getPath();
                    String addIp = addFullPath.substring(addFullPath.lastIndexOf("/") + 1);
                    synchronized (serviceVersionPath) {
                        // 锁住当前的操作对象
                        ipSet = serviceVersionIpsSetMap.get(serviceVersionPath);
                        if (ipSet == null) {
                            ipSet = Collections.synchronizedSet(new TreeSet<>());
                        }
                    }
                    ipSet.add(addIp);
                    break;
                case INITIALIZED:
                    synchronized (serviceVersionPath) {
                        // 锁住当前的操作对象
                        ipSet = serviceVersionIpsSetMap.get(serviceVersionPath);
                        if (ipSet == null) {
                            ipSet = Collections.synchronizedSet(new TreeSet<>());
                        }
                        List<ChildData> childDatas = event.getInitialData();
                        for (ChildData childData : childDatas) {
                            String singleFullPath = childData.getPath();
                            String singleIp = singleFullPath.substring(singleFullPath.lastIndexOf("/") + 1);
                            ipSet.add(singleIp);
                        }
                    }
                    break;
                case CHILD_REMOVED:
                    // 锁住当前的操作对象
                    String removeFullPath = event.getData().getPath();
                    String removeIp = removeFullPath.substring(removeFullPath.lastIndexOf("/") + 1);
                    synchronized (serviceVersionPath) {
                        // 锁住当前的操作对象
                        ipSet = serviceVersionIpsSetMap.get(serviceVersionPath);
                        if (ipSet == null) {
                            ipSet = Collections.synchronizedSet(new TreeSet<>());
                        }
                    }
                    ipSet.remove(removeIp);
                    break;
            }
        }
    }

}

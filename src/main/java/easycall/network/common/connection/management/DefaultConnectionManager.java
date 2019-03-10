package easycall.network.common.connection.management;

import easycall.network.common.connection.Connection;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 默认的连接管理器
 * // TODO 1、Channel通过id能快速获取
 * // TODO 2、能在一组连接中选择空闲的连接进行返回
 * // TODO 3、客户端和服务端有不同的Handler进行连接的添加
 *
 * @author 翁富鑫 2019/3/1 21:21
 */
public class DefaultConnectionManager implements ConnectionManager {

    /**
     * host+port作为key，value为当前key对应的单元创建的Channel的id的列表
     * 注意：connectionId与对应的ChannelId保持一致
     */
    private Map<String, List<String>> hostConnectionIdsMap;
    private final String addConnLock = "lock";
    /**
     * ChannelId(long)以及对应的Connection
     */
    private Map<String, Connection> idConnectionMap;

    /**
     * 最大空闲时间，超过则关闭连接，默认为120秒
     */
    private static final Integer maxIdleTime = 120;

    /**
     * 空闲检测器
     */
    private IdleConnectionRemoveManager idleConnectionRemoveManager;

    public DefaultConnectionManager() {
        hostConnectionIdsMap = new ConcurrentHashMap<>();
        idConnectionMap = new ConcurrentHashMap<>();
        idleConnectionRemoveManager = new IdleConnectionRemoveManager(maxIdleTime);
        init();
    }

    private void init() {
        // 启动空闲连接监听
        new Thread(idleConnectionRemoveManager).start();
    }

    @Override
    public List<Connection> getAllConnections() {
        List<Connection> connections = new ArrayList<>();
        connections.addAll(idConnectionMap.values());
        return connections;
    }

    @Override
    public List<Connection> getConnectionsByIpAndPort(String ip, Integer port) {
        String connectionKey = getConnectionKey(ip, port);
        List<String> connectionIds = hostConnectionIdsMap.get(connectionKey);
        List<Connection> connections = new ArrayList<>();
        if (connectionIds != null && connectionIds.size() > 0) {
            for (String connectionId : connectionIds) {
                connections.add(idConnectionMap.get(connectionId));
            }
        }
        return connections;
    }

    @Override
    public Connection getIdleConnectionByIpAndPort(String ip, Integer port) {
        List<Connection> connections = this.getConnectionsByIpAndPort(ip, port);
        // TODO 使用更优秀的策略获取Connection
        if (connections != null && connections.size() > 0) {
            int size = connections.size();
            // 暂时随机返回一个
            return connections.get(new Random().nextInt(size));
        }
        return null;
    }

    @Override
    public Connection getTargetConnectionByChannelId(String longChannelId) {
        return idConnectionMap.get(longChannelId);
    }

    @Override
    public boolean addConnection(Connection connection) {
        try {
            String ip = connection.getTargetIp();
            Integer port = connection.getTargetPort();
            // 获取主机key
            String hostKey = getConnectionKey(ip, port);
            synchronized (hostKey) {
                List<String> connections = hostConnectionIdsMap.get(hostKey);
                if (connections == null) {
                    // 为当前key创建List
                    List<String> connectionList = new CopyOnWriteArrayList<>();
                    connectionList.add(connection.getConnectionId());
                    hostConnectionIdsMap.put(hostKey, connectionList);
                } else {
                    // 当前key已经存在list，直接添加
                    hostConnectionIdsMap.get(hostKey).add(connection.getConnectionId());
                }
            }
            // 保存ChannelId和对应关系
            idConnectionMap.put(connection.getConnectionId(),connection);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeAllConnections(String ip, Integer port) {
        String hostKey = getConnectionKey(ip, port);
        List<String> connectionIds = hostConnectionIdsMap.get(hostKey);
        for (String connectionId : connectionIds) {
            try {
                // 关闭Channel
                idConnectionMap.get(connectionId).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 删除对应的Connection
            idConnectionMap.remove(connectionId);
        }
        // 解除主机单元与连接id的绑定
        hostConnectionIdsMap.remove(hostKey);
        return true;
    }

    @Override
    public boolean removeConnection(String longChannelId) {
        Connection connection = idConnectionMap.get(longChannelId);
        if (connection == null) {
            return true;
        }
        String hostKey = getConnectionKey(connection.getTargetIp() ,connection.getTargetPort());
        List<String> hostConnectionIds = hostConnectionIdsMap.get(hostKey);
        // 解除主机单元与连接id的绑定
        if (hostConnectionIds != null && hostConnectionIds.size() > 0) {
            hostConnectionIds.remove(longChannelId);
        }
        // 删除对应的Connection
        idConnectionMap.remove(longChannelId);
        try {
            // 关闭连接
            System.out.println("DefaultConnectionManager -- removeConnection() -- 关闭了连接，channelId:" + longChannelId);
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void restConnectionLastWriteTime(String connectionId) {
        idConnectionMap.get(connectionId).resetLastWriteTime(System.currentTimeMillis());
    }

    @Override
    public void close() throws IOException {
        // 关闭所有的连接
        for (Connection connection : idConnectionMap.values()) {
            connection.close();
        }
        // 删除所有的Connection
        idConnectionMap.clear();
        // 解除主机单元与连接id的绑定
        hostConnectionIdsMap.clear();
    }

    private String getConnectionKey(String ip, Integer port) {
        return ip + "-" + port;
    }

    /**
     * 空闲连接管理器，用于自动检测，关闭空闲连接
     * 空闲连接的定义：在指定的时间内没有业务数据的发送，那么表示当前的连接为空闲连接
     */
    public class IdleConnectionRemoveManager implements Runnable {

        /**
         * 连接最大可空闲时间，通过连接写出业务数据的间隔来进行确定，单位秒
         */
        private int maxIdleTime;

        /**
         * ChannelId所对应的Channel的上次写数据时间，单位毫秒
         */
        private Map<String, Long> channelLastWriteTimeMap = new HashMap<>();

        public IdleConnectionRemoveManager(Integer maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        public void resetLastWriteTime(String longChannelId) {
            idConnectionMap.get(longChannelId).resetLastWriteTime(System.currentTimeMillis());
        }

        @Override
        public void run() {
            System.out.println("启动检测");
            while (true) {
                if (idConnectionMap.size() > 0) {
                    long currentTimeMills = System.currentTimeMillis();
                    // 遍历所有的Connection
                    List<String> removeKeyList = new ArrayList<>();
                    for (Connection connection : idConnectionMap.values()) {
                        Long lastWriteTime = connection.getLastWriteTime();
                        String channelId = connection.getConnectionId();
                        // 已经空闲了的时间
                        long idleTime = currentTimeMills - lastWriteTime;
                        if (maxIdleTime * 1000 <= idleTime) {
                            removeConnection(channelId);
                            removeKeyList.add(channelId);
                            System.out.println("空闲检测器关闭了连接");
                        }
                    }
                    // 解除channel的关系
                    for (String channelId : removeKeyList) {
                        removeConnection(channelId);
                    }
                }
                // 每5秒钟检测一次
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

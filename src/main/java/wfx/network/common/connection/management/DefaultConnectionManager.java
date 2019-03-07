package wfx.network.common.connection.management;

import io.netty.channel.Channel;
import wfx.network.common.connection.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 默认的连接管理器
 * // TODO 1、Channel通过id能快速获取
 * // TODO 2、能在一组连接中选择空闲的连接进行返回
 * // TODO 3、客户端和服务端有不同的Handler进行连接的添加
 * @author 翁富鑫 2019/3/1 21:21
 */
public class DefaultConnectionManager implements ConnectionManager {

    private Map<String, List<Connection>> connectionMap;
    private final String addConnLock = "lock";

    public DefaultConnectionManager() {
        connectionMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<Connection> getAllConnections() {
        List<Connection> connections = new ArrayList<>();
        if (connectionMap.size() > 0) {
            for (Map.Entry<String,List<Connection>> entry : connectionMap.entrySet()) {
                connections.addAll(entry.getValue());
            }
        }
        return connections;
    }

    @Override
    public List<Connection> getConnectionsByIpAndPort(String ip, Integer port) {
        String connectionKey = getConnectionKey(ip, port);
        return connectionMap.get(connectionKey);
    }

    @Override
    public Connection getIdleConnectionByIpAndPort(String ip, Integer port) {
        List<Connection> connections = this.getConnectionsByIpAndPort(ip,port);
        // TODO 使用更优秀的策略获取Connection
        if (connections != null && connections.size() > 0) {
            int size = connections.size();
            // 暂时随机返回一个
            return connections.get(new Random().nextInt(size));
        }
        return null;
    }

    @Override
    public Connection getTargetConnectionByChannelId(Integer channelId) {
        throw new RuntimeException("不支持");
    }

    @Override
    public boolean addConnection(Connection connection) {
        try {
            Channel channel = connection.getConnectionChannel();
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            String ip = socketAddress.getHostString();
            Integer port = socketAddress.getPort();
            // map保存的key
            String key = getConnectionKey(ip, port);
            synchronized (addConnLock) {
                List<Connection> connections = connectionMap.get(key);
                if (connections == null) {
                    // 为当前key创建List
                    List<Connection> connectionList = new CopyOnWriteArrayList<>();
                    connectionMap.put(key, connectionList);
                    connectionList.add(connection);
                    return true;
                }
            }
            // 当前key已经存在list
            connectionMap.get(key).add(connection);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeAllConnections(String ip, Integer port) {
        String key = getConnectionKey(ip, port);
        List<Connection> connections = connectionMap.get(key);
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connectionMap.remove(key);
        return true;
    }

    @Override
    public boolean removeConnection(Channel channel) {
        try {
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            String ip = socketAddress.getHostString();
            Integer port = socketAddress.getPort();
            String key = getConnectionKey(ip, port);
            List<Connection> connections = connectionMap.get(key);
            if (connections == null || connections.size() == 0) {
                return true;
            }
            System.out.println("DefaultConnectionManager -- removeConnection() -- 关闭Channel--");
            channel.close();
            // 找到channel所在的Connection然后进行删除
            return connections.removeIf((connection) -> connection.getConnectionChannel().compareTo(channel) == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeConnection(Integer channelId) {
        throw new RuntimeException("不支持");
    }

    @Override
    public void close() throws IOException {
        for (Map.Entry<String, List<Connection>> entry : connectionMap.entrySet()) {
            List<Connection> connections = entry.getValue();
            for (Connection connection : connections) {
                connection.close();
            }
        }
    }

    private String getConnectionKey(String ip, Integer port) {
        return ip + "-" + port;
    }

}

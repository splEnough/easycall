package easycall.network.common.connection.management;

import easycall.network.common.connection.Connection;

import java.io.Closeable;
import java.util.List;

/**
 * 连接管理器，子类需保证线程安全
 * @author 翁富鑫 2019/3/1 20:49
 */
public interface ConnectionManager extends Closeable {

    /**
     * 获取所有的连接
     */
    List<Connection> getAllConnections() ;

    /**
     * 通过ip和port获取目标的连接，可能存在多个连接
     * @param ip 目标ip
     * @param port 目标端口
     * @return 如果存在则返回，否则返回null
     */
    List<Connection> getConnectionsByIpAndPort(String ip, Integer port);

    /**
     * 通过ip和port获取目标的连接
     * @param ip 目标ip
     * @param port 目标端口
     * @return 如果存在多个则返回空闲的那个，如果不存在空闲的那个，则随机返回一个
     */
    Connection getIdleConnectionByIpAndPort(String ip, Integer port);

    /**
     * 通过channel的id获取连接
     * @param longChannelId channelId.asLongText()
     * @return
     */
    Connection getTargetConnectionByChannelId(String longChannelId);

    /**
     * 添加一个新的连接
     * @param connection 要添加的连接
     * @return 添加成功返回true
     */
    boolean addConnection(Connection connection) ;

    /**
     * 关闭Channel，删除与指定ip、port的所有连接
     * @param ip ip
     * @param port port
     * @return
     */
    boolean removeAllConnections(String ip,Integer port);

    /**
     * 通过Channel的id删除某个连接
     * @param longChannelId 要删除的ChannelId (ChannelId.asLongText())
     */
    boolean removeConnection(String longChannelId);

    /**
     * 重置指定连接的上次写数据时间
     */
    void restConnectionLastWriteTime(String connectionId);

}

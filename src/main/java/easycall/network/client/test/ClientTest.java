package easycall.network.client.test;

import easycall.network.client.connection.PooledConnectionFactory;
import easycall.network.common.connection.Connection;
import easycall.network.common.connection.ConnectionFactory;
import easycall.network.common.connection.management.ConnectionManager;
import easycall.network.common.connection.management.DefaultConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * @author 翁富鑫 2019/3/2 19:46
 */
public class ClientTest {
    public static void main(String[] args) throws Exception{
        ConnectionManager connectionManager = new DefaultConnectionManager();
        ConnectionFactory factory = new PooledConnectionFactory(connectionManager);
        Connection connection = factory.buildConnection("127.0.0.1",8888, 5,TimeUnit.SECONDS);
//        TimeUnit.SECONDS.sleep(100);
//        connectionManager.close();
//        factory.close();
    }
}

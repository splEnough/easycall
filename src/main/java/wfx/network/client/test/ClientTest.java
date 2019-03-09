package wfx.network.client.test;

import wfx.network.client.connection.PooledConnectionFactory;
import wfx.network.common.connection.Connection;
import wfx.network.common.connection.ConnectionFactory;
import wfx.network.common.connection.management.ConnectionManager;
import wfx.network.common.connection.management.DefaultConnectionManager;

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

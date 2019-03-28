package easycall.network.client.test;

import easycall.config.ClientInitializer;
import easycall.network.client.ClientBoot;
import easycall.network.client.connection.PooledConnectionFactory;
import easycall.network.common.connection.Connection;
import easycall.network.common.connection.ConnectionFactory;
import easycall.network.common.connection.management.ConnectionManager;
import easycall.network.common.connection.management.DefaultConnectionManager;
import easycall.test.EchoService;

import java.util.concurrent.TimeUnit;

/**
 * @author 翁富鑫 2019/3/2 19:46
 */
public class ClientTest {
    public static void main(String[] args) throws Exception{
//        ConnectionManager connectionManager = new DefaultConnectionManager();
//        ClientInitializer clientInitializer = new ClientInitializer();
//
//        ConnectionFactory factory = new PooledConnectionFactory(connectionManager, null);
//        Connection connection = factory.buildConnection("127.0.0.1",8888, 5,TimeUnit.SECONDS);
//        TimeUnit.SECONDS.sleep(100);
//        connectionManager.close();
//        factory.close();
        String connString = "192.168.85.129:2181,192.168.85.130:2181,192.168.85.131:2181";
        ClientBoot clientBoot = new ClientBoot(connString);
        clientBoot.start();
        EchoService echoService = (EchoService)clientBoot.exportService(EchoService.class);
        System.out.println(echoService.echo("msf"));

    }
}

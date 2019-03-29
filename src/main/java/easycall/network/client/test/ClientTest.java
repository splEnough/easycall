package easycall.network.client.test;

import easycall.boot.ClientBoot;
import easycall.test.EchoService;
import easycall.test.PersonService;

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
        PersonService personService = (PersonService) clientBoot.exportService(PersonService.class);
        System.out.println(personService.getPerson("翁富鑫" , null));
        System.out.println(personService.print(null,  "撒旦"));
        personService.doSomething(null, "uad");
        TimeUnit.SECONDS.sleep(100);
        System.out.println(personService.getPerson("翁富鑫" , 21));
        clientBoot.close();
    }
}

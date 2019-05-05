package easycall.network.client.test;

import easycall.boot.ClientBoot;
import easycall.test.EchoService;
import easycall.test.PersonService;
import easycall.test.vo.Person;

import java.util.List;
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
        EchoService echoService = (EchoService)clientBoot.subscribeService(EchoService.class, null);
//        System.out.println(echoService.echo("msf"));
        PersonService personService = (PersonService) clientBoot.subscribeService(PersonService.class, null);
//        System.out.println(personService.getPerson("翁富鑫" , null));
//        System.out.println(personService.getPerson("翁富鑫" , 21));

        // 返回List
        List<Person> personList = personService.getPersonList("翁富鑫");
        System.out.println(personList);
        for (Person person : personList) {
            System.out.println(person);
        }
        clientBoot.close();
    }
}

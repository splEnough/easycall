package easycall.network.server.test;

import easycall.boot.ServerBoot;
import easycall.serviceconfig.server.RPCProvider;
import easycall.test.EchoService;
import easycall.test.EchoServiceImpl;
import easycall.test.PersonService;
import easycall.test.impl.PersonServiceImpl;

/**
 * @author 翁富鑫 2019/3/26 10:40
 */
public class ServerBootTest {
    public static void main(String[] args) {
        String connString = "192.168.85.129:2181,192.168.85.130:2181,192.168.85.131:2181";
        ServerBoot serverBootStarter = new ServerBoot(connString);
        serverBootStarter.start();
        RPCProvider<EchoService> provider = new RPCProvider<>();
        provider.setInterfaceName(EchoService.class.getName());
        provider.setServiceName(EchoService.class.getTypeName());
        provider.setRpcServiceObject(new EchoServiceImpl());
        provider.setVersion("1.0");


        RPCProvider<PersonService> personServiceRPCProvider = new RPCProvider<>();
        personServiceRPCProvider.setInterfaceName(PersonService.class.getName());
        personServiceRPCProvider.setServiceName(PersonService.class.getTypeName());
        personServiceRPCProvider.setRpcServiceObject(new PersonServiceImpl());
        personServiceRPCProvider.setVersion("1.0");

        serverBootStarter.getRpcProviderManager().exportProvider(personServiceRPCProvider);
        serverBootStarter.getRpcProviderManager().exportProvider(provider);


    }
}
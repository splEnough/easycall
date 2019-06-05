package wfx.test;

import easycall.boot.ServerBoot;
import easycall.serviceconfig.server.RPCProvider;
import wfx.service.EchoService;
import wfx.service.EchoServiceImpl;
import wfx.service.LongTimeCostService;
import wfx.service.LongTimeCostServiceImpl;

/**
 * @author 翁富鑫 2019/5/19 10:32
 */
public class ServerTest {

    public static void main(String[] args) {
        String connString
                = "192.168.85.129:2181,192.168.85.130:2181,192.168.85.131:2181";
        ServerBoot serverBootStarter = new ServerBoot(connString);
        // 启动监听
        serverBootStarter.start();
        // 封装本地提供的EchoService服务
        RPCProvider<EchoService> provider = new RPCProvider<>();
        provider.setInterfaceName(EchoService.class.getName());
        provider.setServiceName(EchoService.class.getTypeName());
        provider.setRpcServiceObject(new EchoServiceImpl());
        provider.setVersion("1.0");

        RPCProvider<LongTimeCostService> handleProvider = new RPCProvider<>();
        handleProvider.setInterfaceName(LongTimeCostService.class.getName());
        handleProvider.setServiceName(LongTimeCostService.class.getTypeName());
        handleProvider.setRpcServiceObject(new LongTimeCostServiceImpl());
        handleProvider.setVersion("1.0");
        // 发布服务
        serverBootStarter.getRpcProviderManager().exportProvider(handleProvider);
        serverBootStarter.getRpcProviderManager().exportProvider(provider);
    }

}

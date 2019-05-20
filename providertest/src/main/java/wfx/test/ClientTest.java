package wfx.test;

import easycall.boot.ClientBoot;
import wfx.service.EchoService;
import wfx.service.LongTimeCostService;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 翁富鑫 2019/5/19 10:28
 */
public class ClientTest {

    public static void main(String[] args) throws Exception {


        String connString =
                "192.168.85.129:2181,192.168.85.130:2181,192.168.85.131:2181";
        ClientBoot clientBoot = new ClientBoot(connString);
        // 启动客户端
        clientBoot.start();
        // 封装目标服务参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("serviceName", LongTimeCostService.class.getName());
        paramMap.put("version", "1.0");
        paramMap.put("timeout", 1L);
        LongTimeCostService service = (LongTimeCostService) clientBoot.subscribeService(LongTimeCostService.class, paramMap);
        TimeUnit.SECONDS.sleep(3);
        for (int i = 0; i < 6; i++) {
            try {
                new Thread(() -> {
                    service.handle("sad");
                }).start();
            } catch (Exception e) {

            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        EchoService echoService =
//                (EchoService)clientBoot.subscribeService(EchoService.class, paramMap);
////        // 调用远程方法
//        System.out.println("Time(s)：" + (System.currentTimeMillis()/1000) + "，发起调用" + echoService.echo("hello world"));
//        for (int i = 0;i < Integer.MAX_VALUE;i++) {
//            echoService.echo("第" + i + "号请求");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        scheduled(echoService);
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        clientBoot.close();

    }

    private static void scheduled(EchoService echoService) throws Exception {
        new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                echoService.echo("第" + i + "号请求");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}

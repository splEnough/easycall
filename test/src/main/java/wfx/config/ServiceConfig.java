//package wfx.config;
//
//import org.springframework.context.annotation.Configuration;
//import wfx.consumer.Consumer;
//import wfx.service.HelloService;
//
///**
// * @author 翁富鑫 2019/4/29 21:58
// */
//@Configuration
//public class ServiceConfig {
//
//    public ServiceConfig() {
//        System.out.println("serviceConfig init --- ");
//    }
//
//    @Consumer(value = "helloService", interfaceName = HelloService.class, version = "1.0", timeout = 1000)
//    private HelloService helloService;
//}

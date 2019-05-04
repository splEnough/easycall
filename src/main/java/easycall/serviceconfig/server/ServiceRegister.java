//package easycall.serviceconfig.server;
//
//import easycall.registercenter.RegisterCenterClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Component;
//
///**
// * 进行本地服务的加载注册
// * @author 翁富鑫 2019/3/24 16:16
// */
//@Deprecated
//@Component
//public class ServiceRegister implements ApplicationListener<ContextRefreshedEvent> {
//
//    @Autowired
//    private RegisterCenterClient registerCenterClient;
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        ApplicationContext applicationContext = event.getApplicationContext();
//        // 获取所有的RPCService的BeanName
//        String[] rpcServiceNames = applicationContext.getBeanNamesForType(RPCProvider.class);
//        for (String rpcServiceName : rpcServiceNames) {
//            // 获取到当前的Bean
//            RPCProvider provider =  (RPCProvider)applicationContext.getBean(rpcServiceName);
//            String serviceName = provider.getServiceName();
//            String version = provider.getVersion();
//            doRegister(serviceName , version);
//        }
//    }
//
//    private void doRegister(String serviceName, String version) {
//        registerCenterClient.registerService(serviceName , version);
//    }
//}

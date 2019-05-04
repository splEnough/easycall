//package easycall.serviceconfig.server;
//
//import easycall.registercenter.RegisterCenterClient;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//
///**
// * 在服务端启动服务的注册和管理
// * @author 翁富鑫 2019/3/25 9:03
// */
//@Deprecated
//public class SpringRpcServiceManager implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
//
//    private ApplicationContext applicationContext;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
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

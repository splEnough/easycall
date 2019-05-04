//package easycall.serviceconfig.server.declaration;
//
//import easycall.serviceconfig.server.RPCProvider;
//import io.netty.util.internal.StringUtil;
//import org.springframework.beans.MutablePropertyValues;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.RootBeanDefinition;
//import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
//import org.springframework.core.type.AnnotationMetadata;
//
//import java.util.Map;
//
///**
// * 封装当前的Bean为一个RPC提供方服务
// *TODO 迁移到Boot-AutoConfigure
// * @author 翁富鑫 2019/3/24 14:29
// */
//@Deprecated
//public class RpcServiceRegister implements ImportBeanDefinitionRegistrar {
//
//    @Override
//    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//        System.out.println("进行RpcServiceRegister类的调用");
//        // 当前的类名
//        String fullClassName = importingClassMetadata.getClassName();
//        Map<String, Object> rpcServiceMetaData = importingClassMetadata.getAnnotationAttributes(RpcService.class.getName());
//        String serviceName;
//        // 获取当前的版本
//        String version = (String) rpcServiceMetaData.get("version");
//        if (StringUtil.isNullOrEmpty(version)) {
//            // 默认的版本
//            version = "1.0";
//        }
//        String[] interfaceNames = importingClassMetadata.getInterfaceNames();
//        // 为每个父接口注册一个RPCProvider
//        for (String interfaceName : interfaceNames) {
//            try {
//                registry.getBeanDefinition(interfaceName);
//                continue;
//            } catch (NoSuchBeanDefinitionException e) {
//            }
//            // 使用父接口名作为serviceName
//            serviceName = interfaceName;
//
//            MutablePropertyValues pvs = new MutablePropertyValues();
//            pvs.add("beanId", interfaceName);
//            pvs.add("interfaceName", serviceName);
//            pvs.add("serviceName", interfaceName);
//            pvs.add("serviceProviderClassName", fullClassName);
//            // 提供调用的Bean的id
//            String providerBeanId = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
//            pvs.add("serviceProviderBeanId", easycall.Util.StringUtil.firstCharToLow(providerBeanId));
//            pvs.add("version", version);
//            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(RPCProvider.class, null, pvs);
//            registry.registerBeanDefinition(interfaceName, rootBeanDefinition);
//        }
//    }
//
//}

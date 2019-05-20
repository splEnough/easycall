//package wfx;
//
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//import wfx.service.EchoServiceSecond;
//
//import java.util.Iterator;
//
//@SpringBootApplication
//public class ProviderTest {
//    public static void main(String[] args) {
//        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(ProviderTest.class , args);
//        Iterator<String> iterable = configurableApplicationContext.getBeanFactory().getBeanNamesIterator();
////        System.out.println("-------------------------");
////        while (iterable.hasNext()) {
////            String name = iterable.next();
////            if (name.contains("echoServiceSecondImpl")) {
////                System.out.println(name);
////            }
////        }
////        String[] strings = configurableApplicationContext.getBeanNamesForType(EchoServiceSecond.class);
////        System.out.println(strings.length);
//    }
//}

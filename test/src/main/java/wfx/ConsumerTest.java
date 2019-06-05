package wfx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wfx.controller.MultiTestController;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ConsumerTest {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConsumerTest.class , args);
        System.out.println("消费端启动成功");
    }
}

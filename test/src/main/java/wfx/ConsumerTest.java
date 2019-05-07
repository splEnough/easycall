package wfx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ImportAutoConfiguration(ProviderTest.class)
@SpringBootApplication
public class ConsumerTest {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerTest.class , args);
    }
}

package wfx.config;

import org.springframework.context.annotation.Configuration;
import wfx.consumer.Consumer;
import wfx.service.EchoService;
import wfx.service.EchoServiceSecond;
import wfx.service.HelloService;

@Configuration
public class ServiceConfig {
    @Consumer(value = "echoService", interfaceName = EchoService.class
            , version = "1.0", timeout = 3000)
    private EchoService echoService;
}


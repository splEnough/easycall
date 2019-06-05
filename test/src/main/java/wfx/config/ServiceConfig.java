package wfx.config;

import org.springframework.context.annotation.Configuration;
import wfx.consumer.Consumer;
import wfx.provider.Provider;
import wfx.service.*;

@Configuration
public class ServiceConfig {
    @Consumer(value = "echoService", interfaceName = EchoService.class
            , version = "1.0", timeout = 3000)
    private EchoService echoService;

    @Consumer(value = "multiTestService" ,interfaceName = MultiTestService.class
            , version = "1.0" , timeout = 3000)
    private MultiTestService multiTestService;

    @Consumer(value = "longTimeCostService", interfaceName = LongTimeCostService.class
            , version = "1.0" , timeout = 3000)
    private LongTimeCostService longTimeCostService;
}


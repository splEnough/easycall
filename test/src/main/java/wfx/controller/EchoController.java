package wfx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wfx.service.EchoService;

import java.util.concurrent.TimeUnit;

/**
 * @author 翁富鑫 2019/5/19 11:15
 */
@Controller
public class EchoController {
    @Autowired
    private EchoService echoService;
    @RequestMapping("/test")
    @ResponseBody
    public Object test(String msg) throws Exception{
        while (true) {
            try {
                System.out.println(echoService.echo(msg));
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                TimeUnit.SECONDS.sleep(4);
            }
        }
    }
}

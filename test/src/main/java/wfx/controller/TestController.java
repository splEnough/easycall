package wfx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wfx.service.EchoService;
import wfx.service.EchoServiceSecond;

/**
 * @author 翁富鑫 2019/5/6 21:26
 */

    @Controller
    public class TestController {
        @Autowired
        private EchoService echoService;
        @RequestMapping("/testtest")
        @ResponseBody
        public Object test(String msg) {
            System.out.println(Thread.currentThread().getName());
            return echoService.echo(msg);
        }
    }

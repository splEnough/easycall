package wfx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wfx.service.EchoService;

/**
 * @author 翁富鑫 2019/5/19 11:15
 */

    @Controller
    public class EchoController {
        @Autowired
        private EchoService echoService;
        @RequestMapping("/test")
        @ResponseBody
        public Object test(String msg) {
            System.out.println(Thread.currentThread().getName());
            return echoService.echo(msg);
        }
    }

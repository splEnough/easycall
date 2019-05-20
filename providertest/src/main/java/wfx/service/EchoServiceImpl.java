package wfx.service;

import wfx.provider.Provider;

/**
 * @author 翁富鑫 2019/5/19 10:20
 */

    @Provider(interfaceClass = EchoService.class)
    public class EchoServiceImpl implements EchoService {
        @Override
        public String echo(String msg) {
            System.out.println("调用线程："
                    + Thread.currentThread().getName()
                    + "，请求参数：" + msg);
            return "echo:" + msg;
        }
    }


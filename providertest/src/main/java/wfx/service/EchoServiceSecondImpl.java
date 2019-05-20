package wfx.service;


import wfx.provider.Provider;

/**
 * @author 翁富鑫 2019/5/5 23:33
 */
@Provider(interfaceClass = EchoServiceSecond.class)
public class EchoServiceSecondImpl implements EchoServiceSecond {
    @Override
    public String echo(String msg) {
        new EchoServiceImpl();
        System.out.println(Thread.currentThread().getName() + " msg :" + msg);
        return "EchoServiceSecondImpl --- " + Thread.currentThread().getName() + " echo:" + msg;
    }
}

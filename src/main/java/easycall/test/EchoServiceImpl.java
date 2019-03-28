package easycall.test;

/**
 * @author 翁富鑫 2019/3/26 10:43
 */
public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String msg) {
        System.out.println("EchoServiceImpl -- 发起了调用：" + Thread.currentThread().getName());
        return "EchoServiceImpl -- echo:" + msg;
    }
}

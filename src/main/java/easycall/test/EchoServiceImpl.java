package easycall.test;

/**
 * @author 翁富鑫 2019/3/26 10:43
 */
public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String msg) {
        return "EchoServiceImpl -- echo:" + msg;
    }
}

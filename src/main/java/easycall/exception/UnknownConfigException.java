package easycall.exception;

/**
 * 未知的配置异常
 * @author 翁富鑫 2019/3/28 16:07
 */
public class UnknownConfigException extends RuntimeException{
    public UnknownConfigException(String msg) {
        super(msg);
    }
}

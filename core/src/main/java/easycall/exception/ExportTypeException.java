package easycall.exception;

/**
 * 订阅服务的时候如果不是订阅的接口，抛出异常
 * @author 翁富鑫 2019/3/28 17:01
 */
public class ExportTypeException extends RuntimeException {
    public ExportTypeException(String msg) {
        super(msg);
    }
}

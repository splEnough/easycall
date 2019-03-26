package easycall.exception;

/**
 * 不支持的操作
 * @author 翁富鑫 2019/3/25 15:58
 */
public class UnSupportedException extends RpcExceptionBase {

    public UnSupportedException(String msg) {
        super(ResultCode.UN_SUPPORTED_ERROR , msg);
    }

}

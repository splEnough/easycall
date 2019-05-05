package easycall.exception;

/**
 * Rpc服务找不到
 * @author 翁富鑫 2019/3/26 11:04
 */
public class RpcServiceNotFoundException extends RpcExceptionBase {

    public RpcServiceNotFoundException(String msg) {
        super(ResultCode.SERVICE_NOT_FOUNT_ERROR, msg);
    }
}

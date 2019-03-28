package easycall.exception;

import static easycall.exception.ResultCode.RPC_CALL_RESPONSE_TIME_OUT;

/**
 * 返回数据超时
 * @author 翁富鑫 2019/3/28 13:43
 */
public class RpcCallResponseTimeOutException extends RpcExceptionBase {

    public RpcCallResponseTimeOutException(String msg) {
        super(RPC_CALL_RESPONSE_TIME_OUT, msg);
    }
}

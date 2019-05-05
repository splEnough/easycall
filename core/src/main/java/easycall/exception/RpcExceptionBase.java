package easycall.exception;

/**
 * 异常父类
 * @author 翁富鑫 2019/3/25 15:48
 */
public abstract class RpcExceptionBase extends Exception{

    /**
     * 错误码
     */
    protected int errorCode ;

    /**
     * 错误信息
     */
    protected String msg ;

    public RpcExceptionBase(ResultCode resultCode, String msg) {
        super(resultCode.getMsg());
        this.errorCode = resultCode.getCode();
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

package easycall.exception;

/**
 * 魔数检查错误
 * @author 翁富鑫 2019/3/25 11:31
 */
public class MagicCheckException extends RpcExceptionBase {

    public MagicCheckException(String msg) {
        super(ResultCode.MAGIC_ERROR , msg);
    }

}

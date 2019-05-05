package easycall.exception;

/**
 * 反序列化出错
 * @author 翁富鑫 2019/3/25 13:27
 */
public class DataDeSerializeException extends RpcExceptionBase {
    public DataDeSerializeException(String msg) {
        super(ResultCode.DE_SERIALIZE_ERROR, msg);
    }
}

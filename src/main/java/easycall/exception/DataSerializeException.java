package easycall.exception;

/**
 * 数据序列化出错
 * @author 翁富鑫 2019/3/25 13:38
 */
public class DataSerializeException extends RpcExceptionBase {
    public DataSerializeException(String msg) {
        super(ResultCode.SERIALIZE_ERROR, msg);
    }
}


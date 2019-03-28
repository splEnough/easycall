package easycall.exception;

/**
 * 返回状态码
 * @author 翁富鑫 2019/3/25 15:55
 */
public enum ResultCode {
    SUCCESS(200, "success"),
    MAGIC_ERROR(201, "魔数校验失败"),
    SERIALIZE_ERROR(201, "序列化失败"),
    DE_SERIALIZE_ERROR(202, "反序列化失败"),
    UN_SUPPORTED_ERROR(203, "不支持的操作"),
    SERVICE_NOT_FOUNT_ERROR(204,"服务不存在"),
    RPC_CALL_RESPONSE_TIME_OUT(305, "调用返回超时"),
    UNKNOWN_ERROR(500 , "unknown error");

    private int code;
    private String msg;

    private ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}

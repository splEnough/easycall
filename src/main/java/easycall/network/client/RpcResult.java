package easycall.network.client;

/**
 * RPC调用返回结果，封装信息，用于RpcMessageManager保存
 * @author 翁富鑫 2019/3/28 19:24
 */
public class RpcResult {

    /**
     * 是否成功
     */
    private boolean success;
    private Long requestId;
    private String resultType;
    /**
     * 返回结果，如果操作成功则为返回的实体，操作失败则为返回的异常栈（String）
     */
    private Object resultObject;

    public RpcResult(Long requestId, String resultType, Object resultObject, boolean success) {
        this.requestId = requestId;
        this.resultObject = resultObject;
        this.success = success;
        this.resultType = resultType;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResultType() {
        return resultType;
    }

}

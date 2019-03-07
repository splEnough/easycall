package wfx.network.common.protocol;

/**
 * 编码协议，规定字节的摆放顺序和位置
 * @author 翁富鑫 2019/3/7 22:12
 */
public class RpcProtocol {
    /**
     * 魔数
     */
    private byte[] magic = "easy".getBytes();

    /**
     * 当前消息的类型，请求为0，响应为1
     */
    private byte type;

    /**
     * 使用的序列化类型，默认为0
     */
    private byte serializeType;

    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 服务名长
     */
    private int serviceNameLength;

    /**
     * 方法名长
     */
    private int methodNameLength;

    /**
     * 方法参数个数
     */
    private int methodParamCount;

    /**
     * 服务名
     */
    private byte[] serviceName;


}

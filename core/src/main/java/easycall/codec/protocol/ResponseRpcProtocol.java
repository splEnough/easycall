package easycall.codec.protocol;

import easycall.codec.packet.MessageType;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 返回数据协议
 * @author 翁富鑫 2019/3/25 13:53
 */
public class ResponseRpcProtocol {
    /**
     * 魔数，四个字节
     */
    public static final byte[] MAGIC = "easy".getBytes();

    /**
     * 当前消息的类型，与MessageType中规定的ordinal对应
     */
    private byte type = (byte) MessageType.SERVICE_DATA_RESPONSE.ordinal();

    /**
     * 当前响应对应的请求的id
     */
    private long requestId;

    /**
     * 使用的序列化类型，默认为0
     */
    private byte serializeType = 0;

    /**
     * 返回状态码
     */
    private int resultCode;

    /**
     * 返回数据类型名长度
     */
    private int returnTypeLength;

    /**
     * 返回对象长度
     */
    private int bodyDataLength;

    /**
     * 返回数据类型名
     */
    private byte[] returnDataTypeName;

    /**
     * 返回对象数据
     */
    private byte[] bodyData;

    public int getReturnTypeLength() {
        return returnTypeLength;
    }

    public void setReturnTypeLength(int returnTypeLength) {
        this.returnTypeLength = returnTypeLength;
    }

    public static byte[] getMAGIC() {
        return MAGIC;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
    }

    public int getBodyDataLength() {
        return bodyDataLength;
    }

    public void setBodyDataLength(int bodyDataLength) {
        this.bodyDataLength = bodyDataLength;
    }

    public byte[] getReturnDataTypeName() {
        return returnDataTypeName;
    }

    public void setReturnDataTypeName(byte[] returnDataTypeName) {
        this.returnDataTypeName = returnDataTypeName;
    }

    public byte[] getBodyData() {
        return bodyData;
    }

    public void setBodyData(byte[] bodyData) {
        this.bodyData = bodyData;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}

package easycall.network.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * 编码协议，规定字节的摆放顺序和位置
 * @author 翁富鑫 2019/3/7 22:12
 */
public class RpcProtocol {
    /**
     * 魔数，四个字节
     */
    public static final byte[] MAGIC = "easy".getBytes();

    /**
     * 当前消息的类型，与MessageType中规定的ordinal对应
     */
    private byte type = 0;

    /**
     * 当前请求的id
     */
    private long requestId;

    /**
     * 使用的序列化类型，默认为0
     */
    private byte serializeType = 0;

    /**
     * 超时时间，默认为3秒
     */
    private long timeout = 3000;

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

    /**
     * 方法名
     */
    private byte[] methodName;

    /**
     * 参数数据长度 在 RpcProtocolCodec.encode()方法中进行计算
     */
    private int bodyDataLength;

    /**
     * 参数及内容信息
     */
    private List<ParamTypeAndValue> paramValues;

    /**
     * 单个参数的信息
     */
    public static class ParamTypeAndValue{

        public ParamTypeAndValue(int paramTypeNameLength, byte[] paramTypeName, int paramValueLength, byte[] paramValue) {
            this.paramTypeNameLength = paramTypeNameLength;
            this.paramTypeName = paramTypeName;
            this.paramValueLength = paramValueLength;
            this.paramValue = paramValue;
        }

        /**
         * 参数类型长度
         */
        private int paramTypeNameLength;

        /**
         * 参数内容长度
         */
        private int paramValueLength;

        /**
         * 参数类型名
         */
        private byte[] paramTypeName;

        /**
         * 参数内容
         */
        private byte[] paramValue;

        private byte[] data;

        public int getParamTypeNameLength() {
            return paramTypeNameLength;
        }

        public void setParamTypeNameLength(int paramTypeNameLength) {
            this.paramTypeNameLength = paramTypeNameLength;
        }

        public byte[] getParamTypeName() {
            return paramTypeName;
        }

        public void setParamTypeName(byte[] paramTypeName) {
            this.paramTypeName = paramTypeName;
        }

        public int getParamValueLength() {
            return paramValueLength;
        }

        public void setParamValueLength(int paramValueLength) {
            this.paramValueLength = paramValueLength;
        }

        public byte[] getParamValue() {
            return paramValue;
        }

        public void setParamValue(byte[] paramValue) {
            this.paramValue = paramValue;
        }

        public byte[] getProtocolData() {
            if (data == null) {
                data = toBytes();
            }
            return data;
        }

        private byte[] toBytes() {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(paramTypeNameLength);
            byteBuf.writeInt(paramValueLength);
            byteBuf.writeBytes(paramTypeName);
            byteBuf.writeBytes(paramValue);
            byte[] data = ByteBufUtil.getBytes(byteBuf);
            ReferenceCountUtil.release(byteBuf);
            return data;
        }

    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public List<ParamTypeAndValue> getParamValues() {
        return paramValues;
    }

    public void setBodyDataLength(int bodyDataLength) {
        this.bodyDataLength = bodyDataLength;
    }

    public void setParamValues(List<ParamTypeAndValue> paramValues) {
        this.paramValues = paramValues;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getServiceNameLength() {
        return serviceNameLength;
    }

    public int getMethodNameLength() {
        return methodNameLength;
    }

    public int getMethodParamCount() {
        return methodParamCount;
    }

    public void setMethodParamCount(int methodParamCount) {
        this.methodParamCount = methodParamCount;
    }

    public byte[] getServiceName() {
        return serviceName;
    }

    public void setServiceName(byte[] serviceName) {
        this.serviceName = serviceName;
        this.serviceNameLength = serviceName.length;
    }

    public byte[] getMethodName() {
        return methodName;
    }

    public void setMethodName(byte[] methodName) {
        this.methodName = methodName;
        this.methodNameLength = methodName.length;
    }

    public void setServiceNameLength(int serviceNameLength) {
        this.serviceNameLength = serviceNameLength;
    }

    public void setMethodNameLength(int methodNameLength) {
        this.methodNameLength = methodNameLength;
    }

    public int getBodyDataLength() {
        return bodyDataLength;
    }

}

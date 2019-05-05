package easycall.codec.packet;

import easycall.codec.serializer.SerializeType;

import java.util.List;

/**
 * @author 翁富鑫 2019/3/7 16:54
 */
public abstract class PacketAdapter implements Packet {
    /**
     * 本次请求的传输数据类型
     */
    protected MessageType messageType;

    /**
     * 序列化类型
     */
    protected SerializeType serializeType;

    /**
     * 传输的对象
     */
    protected List<Object> transObjects;

    /**
     * 传输的对象所一一对应的类型名
     */
    protected List<String> transObjectTypeNames;

    /**
     * 当前请求数据对应的id
     */
    protected long requestId;

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public SerializeType getSerializeType() {
        return serializeType;
    }

    @Override
    public List<Object> getObjects() {
        return transObjects;
    }

    @Override
    public List<String> getObjectTypeNames() {
        return transObjectTypeNames;
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    public void setTransObjectTypeNames(List<String> transObjectTypeNames) {
        this.transObjectTypeNames = transObjectTypeNames;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public void setTransObjects(List<Object> transObjects) {
        this.transObjects = transObjects;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
}

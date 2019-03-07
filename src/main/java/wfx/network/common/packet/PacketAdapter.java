package wfx.network.common.packet;

import wfx.network.common.serializer.SerializeType;

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

}

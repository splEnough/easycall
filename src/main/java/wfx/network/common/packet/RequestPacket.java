package wfx.network.common.packet;

import java.util.List;

/**
 * 请求数据包
 * @author 翁富鑫 2019/3/7 16:33
 */
public class RequsetPacket implements Packet{
    /**
     * 本次请求的传输数据类型
     */
    private MessageType messageType;
    /**
     * 序列化类型
     */
    private SerializeType serializeType;
    /**
     * 传输的数据
     */
    private byte[] data;

    /**
     * 需要传输的对象，用于在本类中进行序列化，然后再传输
     */
    private List<Object> transObjects ;



    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public SerializeType getSerializeType() {
        return serializeType;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}

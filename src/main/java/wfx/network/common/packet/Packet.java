package wfx.network.common.packet;

import wfx.network.common.serializer.SerializeType;

import java.util.List;

/**
 * 一次消息发送的数据包，包含序列化类型、请求类型等等
 * @author 翁富鑫 2019/3/7 16:25
 */
public interface Packet {
    /**
     * 获取请求类型
     */
    MessageType getMessageType();

    /**
     * 获取序列化类型
     */
    SerializeType getSerializeType();

    /**
     * 获取传输的对象
     */
    List<Object> getObjects();
}

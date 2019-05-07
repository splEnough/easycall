package easycall.codec.packet;

import easycall.codec.serializer.SerializeType;

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

    /**
     * 获取传输对象所一一对应的类型名
     */
    List<String> getObjectTypeNames();

    /**
     * 获取当前数据对应的请求的id
     */
    long getRequestId();
}

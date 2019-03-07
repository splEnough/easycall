package wfx.network.common.packet;

import wfx.network.common.serializer.SerializeType;

import java.util.List;

/**
 * 进行请求数据包的封装
 * @author 翁富鑫 2019/3/7 16:50
 */
public class PacketResolver {
    /**
     * 将参数封装为请求数据包
     * @param transObjects 要传输的对象
     * @param serializeType 序列化的类型
     * @param messageType 消息类型
     * @param service 目标服务（接口）
     * @param method 目标方法（方法名）
     * @param version 目标版本
     * @return
     */
    public RequestPacket encodeRequestPacket(MessageType messageType, SerializeType serializeType, String service, String method, String version, List<Object> transObjects) {
        return new RequestPacket(messageType,serializeType,service,method,version,transObjects);
    }

    public ResponsePacket encodeResponsePacket() {
        // TODO
        return null;
    }
}

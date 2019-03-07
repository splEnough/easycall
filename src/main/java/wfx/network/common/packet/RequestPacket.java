package wfx.network.common.packet;

import wfx.network.common.serializer.SerializeType;

import java.util.List;

/**
 * 请求数据包，只负责进行请求数据的保存，解析操作交由其他类完成
 * @author 翁富鑫 2019/3/7 16:33
 */
public class RequestPacket extends PacketAdapter{
    /**
     * 目标服务名
     */
    private String targetService;
    /**
     * 目标方法名
     */
    private String targetMethod;
    /**
     * 目标服务版本
     */
    private String targetVersion;

    public RequestPacket(MessageType messageType, SerializeType serializeType, String targetService, String targetMethod, String targetVersion, List<Object> transObjects) {
        this.messageType = messageType;
        this.serializeType = serializeType;
        this.targetService = targetService;
        this.targetMethod = targetMethod;
        this.targetVersion = targetVersion;
        this.transObjects = transObjects;
    }

}

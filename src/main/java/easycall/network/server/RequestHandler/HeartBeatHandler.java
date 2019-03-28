package easycall.network.server.RequestHandler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.MessageType;
import easycall.codec.packet.RequestPacket;
import easycall.codec.packet.ResponsePacket;
import easycall.codec.serializer.SerializeType;
import easycall.exception.ResultCode;
import easycall.config.ServerInitializer;
import easycall.serviceconfig.server.RPCProvider;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 回复心跳数据
 * @author 翁富鑫 2019/3/25 20:05
 */
public class HeartBeatHandler extends RequestHandlerBase {

    private static final String HEARTBEAT_SEQUENCE = "HEART_BEAT_RESPONSE";

    private static final SerializeType HEARTBEAT_SERIALIZE_TYPE = SerializeType.PROTO_STUFF;

    public HeartBeatHandler(Channel channel, RequestPacket packet, RPCProvider<?> rpcProvider, ServerInitializer serverInitializer) {
        super(channel, packet, rpcProvider , serverInitializer);
    }

    @Override
    public void run() {
        ByteBuf result = null;
        try {
            ResponsePacket responsePacket = new ResponsePacket();
            responsePacket.setMessageType(MessageType.HEARTBEAT_RESPONSE);
            responsePacket.setSerializeType(HEARTBEAT_SERIALIZE_TYPE);
            List<Object> transObjects = new ArrayList<>();
            transObjects.add(HEARTBEAT_SEQUENCE);
            responsePacket.setTransObjects(transObjects);
            // 响应所属请求的id
            responsePacket.setRequestId(requestPacket.getRequestId());
            List<String> paramTypeNames = new ArrayList<>();
            paramTypeNames.add(String.class.getTypeName());
            responsePacket.setTransObjectTypeNames(paramTypeNames);
            // 发送心跳响应
            result = Framer.encode(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        channel.writeAndFlush(result);
    }

    private ResponsePacket errorPacket(ResultCode resultCode) {
        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.setResultCode(resultCode.getCode());
        responsePacket.setMessageType(MessageType.HEARTBEAT_RESPONSE);
        responsePacket.setRequestId(requestPacket.getRequestId());
        responsePacket.setSerializeType(SerializeType.PROTO_STUFF);
        return responsePacket;
    }
}

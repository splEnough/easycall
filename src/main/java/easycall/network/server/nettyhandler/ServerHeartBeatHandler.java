package easycall.network.server.nettyhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import easycall.network.common.frame.Framer;
import easycall.network.common.packet.MessageType;
import easycall.network.common.packet.Packet;
import easycall.network.common.packet.RequestPacket;
import easycall.network.common.packet.ResponsePacket;
import easycall.network.common.serializer.SerializeType;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端对于客户端发送的心跳数据的处理类
 * @author 翁富鑫 2019/3/10 11:34
 */
public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {

    private static final String HEARTBEAT_SEQUENCE = "HEART_BEAT_RESPONSE";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = Framer.decode((ByteBuf) msg);
        if (packet instanceof RequestPacket) {
            RequestPacket requestPacket = (RequestPacket) packet;
            switch (packet.getMessageType()) {
                case HEARTBEAT_REQUEST:
                    handlerRequest(requestPacket);
                    ResponsePacket responsePacket = new ResponsePacket();
                    responsePacket.setMessageType(MessageType.HEARTBEAT_RESPONSE);
                    // 心跳数据使用KRYO进行序列化
                    responsePacket.setSerializeType(SerializeType.KRYO);
                    List<Object> transObjects = new ArrayList<>();
                    transObjects.add(HEARTBEAT_SEQUENCE);
                    responsePacket.setTransObjects(transObjects);
                    // 响应所属请求的id
                    responsePacket.setRequestId(packet.getRequestId());
                    List<String> paramTypeNames = new ArrayList<>();
                    paramTypeNames.add(String.class.getTypeName());
                    responsePacket.setParamTypeNames(paramTypeNames);
                    // 发送心跳响应
                    ctx.writeAndFlush(Framer.encode(responsePacket));
                    break;
                case SERVICE_DATA_REQUEST:
                    handlerRequest(requestPacket);
                    break;
            }
        } else {
            throw new Exception("不支持的请求数据");
        }
        super.channelRead(ctx, msg);
    }

    private void handlerRequest(RequestPacket requestPacket) {
        // 传输的对象
        List<Object> objectList = requestPacket.getObjects();
        // 对象对应的类型
        List<String> paramTypeNames = requestPacket.getObjectTypeNames();
        int size = objectList.size();
        for (int i = 0 ; i < size;i++) {
            System.out.print(i + ": type:" + paramTypeNames.get(i) + ",value:" + objectList.get(i));
        }
        System.out.println();
    }
}
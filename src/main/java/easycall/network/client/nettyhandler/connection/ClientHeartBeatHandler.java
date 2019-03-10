package easycall.network.client.nettyhandler.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import easycall.network.common.connection.management.ConnectionManager;
import easycall.network.common.frame.Framer;
import easycall.network.common.packet.MessageType;
import easycall.network.common.packet.Packet;
import easycall.network.common.packet.RequestPacket;
import easycall.network.common.packet.ResponsePacket;
import easycall.network.common.serializer.SerializeType;

import java.util.ArrayList;
import java.util.List;

/**
 * 心跳数据处理器，负责接受心跳数据、发送心跳数据
 * @author 翁富鑫 2019/3/3 16:23
 */
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter{

    /**
     * 持有一个连接管理器
     */
    private ConnectionManager connectionManager;

    /**
     * 心跳包的默认数据
     */
    private static final String HEARTBEAT_SERVICE = "heartbeat";
    private static final String HEARTBEAT_METHOD = "heartbeat";
    /**
     * 心跳数据默认使用protostuff进行序列化
     */
    private static final SerializeType HEARTBEAT_SERIALIZE_TYPE = SerializeType.PROTO_STUFF;


    public ClientHeartBeatHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = Framer.decode((ByteBuf) msg);
        if (packet instanceof RequestPacket) {
            throw new Exception("不支持的请求类型");
        }
        ResponsePacket responsePacket = (ResponsePacket)packet;
        if (packet.getMessageType() == MessageType.HEARTBEAT_RESPONSE) {
            // 心跳返回数据，不做特殊处理
            handleHeartbeatResponse(responsePacket);
            // 释放内存
            ReferenceCountUtil.release(msg);
        } else {
            // 业务数据不在当前的Handler中做处理
            super.channelRead(ctx, msg);
        }
    }

    /**
     * 心跳数据
     */
    private static final String HEARTBEAT_SEQUENCE = "HEARTBEAT";

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            // 只处理读空闲
            switch (((IdleStateEvent) evt).state()) {
                case READER_IDLE:
                    // 读空闲时间超时，关闭连接
                    // （读空闲时间大于写空闲时间，如果读空闲时间周期内还没有读取到数据，表示超时了，服务端可能宕机，网络可能不好）
                    connectionManager.removeConnection(channel.id().asLongText());
                    break;
                case WRITER_IDLE:
                    // 写空闲时间超时，发送心跳数据
                    try {
                        // 心跳数据封装为Packet
                        RequestPacket packet = new RequestPacket();
                        packet.setMessageType(MessageType.HEARTBEAT_REQUEST);
                        List<Object> transObjects = new ArrayList<>();
                        transObjects.add(HEARTBEAT_SEQUENCE);
                        List<String> paramTypeNames = new ArrayList<>();
                        paramTypeNames.add(String.class.getTypeName());
                        packet.setParamTypeNames(paramTypeNames);
                        // 心跳数据的目标业务和方法名，默认值
                        packet.setTargetService(HEARTBEAT_SERVICE);
                        packet.setTargetMethod(HEARTBEAT_METHOD);
                        packet.setTransObjects(transObjects);
                        packet.setSerializeType(HEARTBEAT_SERIALIZE_TYPE);
                        // 发送心跳数据
                        ByteBuf result = Framer.encode(packet);
                        ctx.writeAndFlush(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    private void handleHeartbeatResponse(ResponsePacket responsePacket) {
        // 传输的对象
        List<Object> objectList = responsePacket.getObjects();
        // 对象对应的类型
        List<String> paramTypeNames = responsePacket.getObjectTypeNames();
        int size = objectList.size();
        for (int i = 0 ; i < size;i++) {
            System.out.println(i + ": type:" + paramTypeNames.get(i) + ",value:" + objectList.get(i));
        }
    }
}

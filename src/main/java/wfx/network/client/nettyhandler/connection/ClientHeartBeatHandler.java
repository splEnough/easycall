package wfx.network.client.nettyhandler.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import wfx.network.common.connection.management.ConnectionManager;
import wfx.network.common.frame.Framer;
import wfx.network.common.packet.MessageType;
import wfx.network.common.packet.RequestPacket;
import wfx.network.common.serializer.SerializeType;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送心跳的Handler，TODO 存在的问题：永远会保活，因为一直会发送心跳，需要增加一个机制能让其在指定时间内无业务数据发送时，关闭连接
 * @author 翁富鑫 2019/3/3 16:23
 */
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter{

    /**
     * 持有一个连接管理器
     */
    private ConnectionManager connectionManager;

    public ClientHeartBeatHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * 心跳数据
     */
    private static final String HEARTBEAT_SEQUENCE = "HEARTBEAT";

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("触发事件");
        // TODO 服务端实现心跳数据回复
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            // 只处理读空闲
            switch (((IdleStateEvent) evt).state()) {
                case READER_IDLE:
                    // 读空闲时间超时，关闭连接
                    // （读空闲时间大于写空闲时间，如果读空闲时间周期内还没有读取到数据，表示超时了，服务端可能宕机，网络可能不好）
                    connectionManager.removeConnection(channel);
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
                        // 心跳数据的目标业务和方法名
                        packet.setTargetService("heartbeat");
                        packet.setTargetMethod("heartbeat");
                        packet.setTransObjects(transObjects);
                        // 心跳数据使用KRYO进行序列化
                        packet.setSerializeType(SerializeType.KRYO);
                        // 发送心跳数据
                        ByteBuf result = Framer.encode(packet);
                        System.out.println("写出的数据：" + ByteBufUtil.hexDump(result));
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
}

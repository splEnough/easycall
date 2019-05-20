package easycall.network.client.nettyhandler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.MessageType;
import easycall.codec.packet.Packet;
import easycall.codec.packet.RequestPacket;
import easycall.codec.packet.ResponsePacket;
import easycall.codec.serializer.SerializeType;
import easycall.network.client.management.ConnectionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 心跳数据处理器，负责接受心跳数据、发送心跳数据
 *
 * @author 翁富鑫 2019/3/3 16:23
 */
public class ClientHeartBeatHandler extends ChannelDuplexHandler implements Closeable {

    /**
     * 持有一个连接管理器
     */
    private ConnectionManager connectionManager;

    /**
     * 心跳包的默认数据
     */
    private static final String HEARTBEAT_SERVICE = "heartbeat";
    private static final String HEARTBEAT_METHOD = "heartbeat";
    private static final String HEARTBEAT_VERSION = "1.0";

    /**
     * 心跳数据默认使用protostuff进行序列化
     */
    private static final SerializeType HEARTBEAT_SERIALIZE_TYPE = SerializeType.PROTO_STUFF;

    /**
     * 心跳数据处理线程
     */
    private ExecutorService heartBeatExecutorService = Executors.newSingleThreadExecutor();

    public ClientHeartBeatHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        if (byteBuf.getByte(4) == MessageType.SERVICE_DATA_REQUEST.ordinal()) {
            // 重置上次写数据的时间
            connectionManager.resetConnectionLastWriteTime(ctx.channel().id().asLongText());
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 拷贝一份数据，用于进行异步处理，不影响msg在PipeLine主干中的释放
        ByteBuf inputByteBuf = ((ByteBuf) msg).copy();
        // 判断是否是心跳数据
        byte type = inputByteBuf.getByte(4);
        if (MessageType.getTypeByOrdinal((int) type) == MessageType.HEARTBEAT_RESPONSE) {
            // 只处理心跳
            heartBeatExecutorService.submit(() -> {
                try {
                    Packet packet = Framer.decode(inputByteBuf);
                    ReferenceCountUtil.release(packet);
                    if (packet instanceof RequestPacket) {
                        throw new Exception("不支持的请求类型");
                    }
                    ResponsePacket responsePacket = (ResponsePacket) packet;
                    // 心跳返回数据，不做特殊处理
                    handleHeartbeatResponse(ctx, responsePacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            // 非心跳数据，往PipeLine继续传输
            super.channelRead(ctx,msg);
        }
    }

    /**
     * 心跳数据
     */
    private static final String HEARTBEAT_SEQUENCE = "HEARTBEAT";

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            heartBeatExecutorService.submit(() -> {
                Channel channel = ctx.channel();
                switch (((IdleStateEvent) evt).state()) {
                    case READER_IDLE:
                        // 读空闲时间超时，关闭连接
                        // （读空闲时间大于写空闲时间，如果读空闲时间周期内还没有读取到数据，表示超时了，服务端可能宕机，网络可能不好）
                        connectionManager.removeConnection(channel.id().asLongText());
                        break;
                    case WRITER_IDLE:
                        // 写空闲时间超时，发送心跳数据
                        ByteBuf result = null;
                        try {
                            // 心跳数据封装为Packet
                            RequestPacket packet = new RequestPacket();
                            packet.setMessageType(MessageType.HEARTBEAT_REQUEST);
                            List<Object> transObjects = new ArrayList<>();
                            transObjects.add(HEARTBEAT_SEQUENCE);
                            List<String> paramTypeNames = new ArrayList<>();
                            paramTypeNames.add(String.class.getTypeName());
                            packet.setTransObjectTypeNames(paramTypeNames);
                            // 心跳数据的目标业务和方法名，默认值
                            packet.setTargetService(HEARTBEAT_SERVICE);
                            packet.setTargetVersion(HEARTBEAT_VERSION);
                            packet.setTargetMethod(HEARTBEAT_METHOD);
                            packet.setTransObjects(transObjects);
                            packet.setSerializeType(HEARTBEAT_SERIALIZE_TYPE);
                            System.out.println("Time(s)：" + (System.currentTimeMillis()/1000) + "，channel（" + channel.id().asLongText().substring(0,4) +  "）发送心跳：" + HEARTBEAT_SEQUENCE);
                            // 发送心跳数据
                            result = Framer.encode(packet);
                            ctx.writeAndFlush(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }) ;
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    private void handleHeartbeatResponse(ChannelHandlerContext ctx, ResponsePacket responsePacket) {
//         do nothing
        // 传输的对象
        List<Object> objectList = responsePacket.getObjects();
        // 对象对应的类型
        List<String> paramTypeNames = responsePacket.getObjectTypeNames();
        int size = objectList.size();
        for (int i = 0; i < size; i++) {
            System.out.println("Time(s)：" + (System.currentTimeMillis()/1000) + "，channel（" + ctx.channel().id().asLongText().substring(0,4) + "）收到心跳回复：" + objectList.get(i));
//            System.out.println(i + ": type:" + paramTypeNames.get(i) + ",value:" + objectList.get(i));
        }
    }

    @Override
    public void close() throws IOException {
        this.heartBeatExecutorService.shutdown();
    }
}

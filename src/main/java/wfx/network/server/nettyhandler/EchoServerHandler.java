package wfx.network.server.nettyhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import wfx.network.common.frame.Framer;
import wfx.network.common.packet.Packet;
import wfx.network.common.packet.RequestPacket;

import java.util.List;

/**
 * @author 翁富鑫 2019/3/3 11:10
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有新的客户端连接");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = Framer.decode((ByteBuf) msg);
        if (packet instanceof RequestPacket) {
            RequestPacket requestPacket = (RequestPacket) packet;
            switch (packet.getMessageType()) {
                case HEARTBEAT_REQUEST:
                    handlerRequest(requestPacket);
                    // TODO 实现服务端的回应
                    break;
                case SERVICE_DATA_REQUEST:
                    handlerRequest(requestPacket);break;
            }
        } else {
            throw new Exception("不支持的请求数据");
        }
//        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("哈哈我是服务端".getBytes()));
        // TODO 心跳数据不会继续往下传输，所以在接下来的ChannelHandler中可以添加一个计时器用于进行业务数据发送时间间隔的监听
    }

    private void handlerRequest(RequestPacket requestPacket) {
        // 传输的对象
        List<Object> objectList = requestPacket.getObjects();
        // 对象对应的类型
        List<String> paramTypeNames = requestPacket.getObjectTypeNames();
        System.out.println("读取到的数据：");
        int size = objectList.size();
        for (int i = 0 ; i < size;i++) {
            System.out.println(i + ": type:" + paramTypeNames.get(i) + ",value:" + objectList.get(i));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}

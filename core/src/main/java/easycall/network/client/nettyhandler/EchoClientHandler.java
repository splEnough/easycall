package easycall.network.client.nettyhandler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.MessageType;
import easycall.codec.packet.RequestPacket;
import easycall.codec.serializer.SerializeType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 翁富鑫 2019/3/3 11:13
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoClientHandler -- channelActive() -- 连接成功");
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setTargetMethod("echo");
        requestPacket.setTargetVersion("1.0");
        requestPacket.setSerializeType(SerializeType.PROTO_STUFF);
        requestPacket.setTimeout(10);
        List<Object> transObjects = new ArrayList<>();
        transObjects.add("你好我是客户端");
        requestPacket.setTransObjects(transObjects);
        List<String> objectTypeNames = new ArrayList<>();
        objectTypeNames.add(String.class.getTypeName());
        requestPacket.setTransObjectTypeNames(objectTypeNames);
        requestPacket.setMessageType(MessageType.SERVICE_DATA_REQUEST);
        requestPacket.setRequestId(1000l);
        requestPacket.setTargetService("easycall.test.EchoService");
        ctx.writeAndFlush(Framer.encode(requestPacket));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("EchoClientHandler -- channelRead（） -- 服务端：" + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
        super.channelRead(ctx, msg);
    }
}

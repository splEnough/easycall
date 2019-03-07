package wfx.network.server.nettyhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

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
        System.out.println("读取到客户端的信息：" + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("哈哈我是服务端".getBytes()));
        super.channelRead(ctx, msg);
    }
}

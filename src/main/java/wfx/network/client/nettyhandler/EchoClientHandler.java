package wfx.network.client.nettyhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;

/**
 * @author 翁富鑫 2019/3/3 11:13
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("EchoClientHandler -- channelActive() -- 连接成功");
        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("你好我是客户端".getBytes()));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("EchoClientHandler -- channelRead（） -- 服务端：" + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
        super.channelRead(ctx, msg);
    }
}

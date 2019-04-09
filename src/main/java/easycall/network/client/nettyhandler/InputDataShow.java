package easycall.network.client.nettyhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author 翁富鑫 2019/3/28 20:02
 */
public class InputDataShow extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InputDataShow -- 读取到了数据");
        super.channelRead(ctx, msg);
    }
}

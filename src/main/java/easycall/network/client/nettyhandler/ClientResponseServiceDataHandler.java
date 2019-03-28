package easycall.network.client.nettyhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * TODO 处理返回的业务数据
 * @author 翁富鑫 2019/3/26 17:10
 */
public class ClientResponseServiceDataHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}

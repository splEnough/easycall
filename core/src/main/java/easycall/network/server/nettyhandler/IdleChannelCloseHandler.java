package easycall.network.server.nettyhandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 处理空闲连接事件，关闭空闲连接
 * @author 翁富鑫 2019/3/3 10:47
 */
public class IdleChannelCloseHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();

            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                // 读空闲时间超时，直接关闭Channel
                channel.close();
            }
            // 其他事件不做处理，因为只启用了reader_idle
        }
        super.userEventTriggered(ctx, evt);
    }
}

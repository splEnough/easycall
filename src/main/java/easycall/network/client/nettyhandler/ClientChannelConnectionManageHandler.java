package easycall.network.client.nettyhandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import easycall.network.common.connection.management.ConnectionManager;

/**
 * 用于处理空闲Channel，关闭Connection和Channel
 * @author 翁富鑫 2019/3/2 15:13
 */
@Deprecated
public class ClientChannelConnectionManageHandler extends ChannelDuplexHandler {

    private ConnectionManager connectionManager;

    public ClientChannelConnectionManageHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();

            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                // 读空闲时间超时
                connectionManager.removeConnection(channel.id().asLongText());
            } else if (((IdleStateEvent) evt).state() ==  IdleState.WRITER_IDLE) {
                // 写空闲时间超时
                connectionManager.removeConnection(channel.id().asLongText());
                return;
            } else {
                // 所有时间超时
                connectionManager.removeConnection(channel.id().asLongText());
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}

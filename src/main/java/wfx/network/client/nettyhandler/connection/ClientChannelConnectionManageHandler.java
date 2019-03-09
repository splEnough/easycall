package wfx.network.client.nettyhandler.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import wfx.network.common.connection.management.ConnectionManager;

import java.net.InetSocketAddress;

/**
 * 用于处理空闲Channel，关闭Connection和Channel
 * @author 翁富鑫 2019/3/2 15:13
 */
public class ClientChannelConnectionManageHandler extends ChannelDuplexHandler {

    private ConnectionManager connectionManager;

    public ClientChannelConnectionManageHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // TODO 处理三种事件

            Channel channel = ctx.channel();

            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                // 读空闲时间超时
                connectionManager.removeConnection(channel);
            } else if (((IdleStateEvent) evt).state() ==  IdleState.WRITER_IDLE) {
                // 写空闲时间超时
                connectionManager.removeConnection(channel);
                return;
            } else {
                // 所有时间超时
                connectionManager.removeConnection(channel);
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}

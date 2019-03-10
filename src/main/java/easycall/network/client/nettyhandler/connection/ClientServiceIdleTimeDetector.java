package easycall.network.client.nettyhandler.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import easycall.network.common.connection.management.ConnectionManager;
import easycall.network.common.packet.MessageType;

/**
 * 用于更新当前的连接的空闲时间
 * @author 翁富鑫 2019/3/10 12:14
 */
public class ClientServiceIdleTimeDetector extends ChannelOutboundHandlerAdapter {

    private ConnectionManager connectionManager;

    public ClientServiceIdleTimeDetector(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        if (byteBuf.getByte(5) == MessageType.SERVICE_DATA_REQUEST.ordinal()) {
            connectionManager.removeConnection(ctx.channel().id().asLongText());
        }
        super.write(ctx, msg, promise);
    }
}

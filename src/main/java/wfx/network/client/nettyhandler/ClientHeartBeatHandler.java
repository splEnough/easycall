package wfx.network.client.nettyhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import wfx.network.common.connection.management.ConnectionManager;

/**
 * @author 翁富鑫 2019/3/3 16:23
 */
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter{

    /**
     * 持有一个连接管理器
     */
    private ConnectionManager connectionManager;

    public ClientHeartBeatHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * 心跳数据
     */
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT".getBytes()));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // TODO 服务端实现心跳数据回复
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            // 只处理读空闲
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                // 读空闲时间超时，关闭连接
                // （读空闲时间大于写空闲时间，如果读空闲时间周期内还没有读取到数据，表示超时了，服务端可能宕机，网络可能不好）
                connectionManager.removeConnection(channel);
            } else if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                // 写空闲时间超时，发送心跳数据 TODO 封装为Packet
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}

package easycall.network.common.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;

/**
 * 魔数校验器，如果魔数错误，不继续传递Handler
 * @author 翁富鑫 2019/3/26 16:46
 */
public class MagicCheckHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] magicData = new byte[4];
        byteBuf.getBytes(0 , magicData);
        if (Arrays.equals(magicData , "easy".getBytes())) {
            // 魔数正确
            super.channelRead(ctx , msg);
        } else {
            System.out.println("魔数错误，不继续往下做处理");
        }
    }
}

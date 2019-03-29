package easycall.network.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;

/**
 * 魔数校验器，如果魔数错误，不继续传递Handler
 * @author 翁富鑫 2019/3/26 16:46
 */
public class MagicCheckHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] magicData = new byte[4];
        byteBuf.getBytes(0 , magicData);
        if (!Arrays.equals(magicData , "easy".getBytes())) {
            System.out.println("魔数错误，不继续往下做处理");
        } else {
            super.channelRead(ctx, msg);
        }
    }
}

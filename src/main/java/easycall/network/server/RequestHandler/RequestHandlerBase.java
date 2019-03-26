package easycall.network.server.RequestHandler;

import easycall.codec.packet.RequestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * 请求数据处理基类
 * @author 翁富鑫 2019/3/25 20:01
 */
public abstract class RequestHandlerBase implements Runnable {

    protected Channel channel;
    protected RequestPacket requestPacket;

    protected RequestHandlerBase(Channel channel , RequestPacket packet) {
        this.channel = channel;
        this.requestPacket = packet;
    }

}

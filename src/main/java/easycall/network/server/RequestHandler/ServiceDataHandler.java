package easycall.network.server.RequestHandler;

import easycall.codec.packet.RequestPacket;
import io.netty.channel.Channel;

/**
 * 回复业务数据
 * @author 翁富鑫 2019/3/25 20:58
 */
public class ServiceDataHandler extends RequestHandlerBase {
    protected ServiceDataHandler(Channel channel, RequestPacket packet) {
        super(channel, packet);
    }

    @Override
    public void run() {

    }
}

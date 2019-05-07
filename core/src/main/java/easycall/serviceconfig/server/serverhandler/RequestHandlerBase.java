package easycall.serviceconfig.server.serverhandler;

import easycall.codec.packet.RequestPacket;
import easycall.initconfig.ServerParam;
import easycall.serviceconfig.server.RPCProvider;
import io.netty.channel.Channel;

/**
 * 请求数据处理基类
 * @author 翁富鑫 2019/3/25 20:01
 */
public abstract class RequestHandlerBase implements Runnable {

    protected Channel channel;
    protected RequestPacket requestPacket;
    protected RPCProvider<?> rpcProvider;
    protected ServerParam serverParam;

    protected RequestHandlerBase(Channel channel , RequestPacket packet, RPCProvider rpcProvider, ServerParam serverParam) {
        this.channel = channel;
        this.requestPacket = packet;
        this.rpcProvider = rpcProvider;
        this.serverParam = serverParam;
    }

}

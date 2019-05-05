package easycall.network.client.nettyhandler;

import easycall.serviceconfig.client.RpcMessageManager;
import easycall.serviceconfig.client.RpcResponseHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理返回的业务数据
 * @author 翁富鑫 2019/3/26 17:10
 */
public class ClientResponseServiceDataHandler extends ChannelInboundHandlerAdapter implements Closeable {

    private RpcMessageManager rpcMessageManager;

    public ClientResponseServiceDataHandler(RpcMessageManager rpcMessageManager) {
        this.rpcMessageManager = rpcMessageManager;
    }

    /**
     * 具体的处理线程
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inputByteBuf = ((ByteBuf) msg).copy();
        executorService.submit(new RpcResponseHandler(inputByteBuf, rpcMessageManager));
        super.channelRead(ctx, msg);
    }

    @Override
    public void close() throws IOException {
        this.executorService.shutdown();
    }

}

package easycall.network.server.nettyhandler;

import easycall.thread.ExecutorManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务端对于客户端发送的数据处理器
 * 处理心跳、业务数据
 * @author 翁富鑫 2019/3/10 11:34
 */
public class RequestDataHandlerDispatcher extends SimpleChannelInboundHandler {

    private ExecutorManager executorManager;

    public RequestDataHandlerDispatcher(ExecutorManager executorManager) {
        this.executorManager = executorManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 在这个地方进行处理线程的交接，处理线程处理完毕之后将处理的结果写回，客户端通过requestId进行返回结果的分配
        new RequestDataHandlerDispatcherProxy(ctx.channel() , (ByteBuf) msg, executorManager).handle();
    }

}

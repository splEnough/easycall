package easycall.network.client.nettyhandler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.Packet;
import easycall.codec.packet.ResponsePacket;
import easycall.exception.DataDeSerializeException;
import easycall.exception.MagicCheckException;
import easycall.exception.ResultCode;
import easycall.network.client.RpcResult;
import easycall.serviceconfig.client.RpcMessageManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理返回的业务数据
 * TODO Connection中需要保存所有的ChannelHandler实例，以便在关闭Connection的时候调用close()方法释放资源
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
        executorService.submit(() -> {
            Exception error = null;
            RpcResult rpcResult = null;
            Packet packet = null;
            try {
                packet = Framer.decode(inputByteBuf);
                ReferenceCountUtil.release(inputByteBuf);
            } catch (DataDeSerializeException e) {
                error = e;
            } catch (Exception e) {
                error = e;
                e.printStackTrace();
            }
            if (error != null) {
                // 本地发生了异常
                String errorMessage = getErrorStack(error);
                rpcResult = new RpcResult(packet.getRequestId(), String.class, errorMessage, false);
            } else {
                // 本地没发生异常
                if (!(packet instanceof ResponsePacket)) {
                    // 错误包，不做处理
                } else {
                    ResponsePacket responsePacket = (ResponsePacket) packet;
                    int resultCode = responsePacket.getResultCode();
                    List<String> resultTypeNames = responsePacket.getObjectTypeNames();
                    List<Object> resultObjects = responsePacket.getObjects();
                    // 返回的数据只有一个
                    String resultType = resultTypeNames.get(0);
                    Object resultObject = resultObjects.get(0);
                    try {
                        if (ResultCode.SUCCESS.getCode() != resultCode) {
                            // 调用失败
                            rpcResult = new RpcResult(responsePacket.getRequestId(), Class.forName(resultType), resultObject, false);
                        } else {
                            rpcResult = new RpcResult(responsePacket.getRequestId(), Class.forName(resultType), resultObject, true);
                        }
                    } catch (Exception e) {
                        String errorMessage = getErrorStack(e);
                        rpcResult = new RpcResult(packet.getRequestId(), String.class, errorMessage, false);
                    }
                }
            }
            this.rpcMessageManager.addRpcResult(packet.getRequestId(), rpcResult);
        });
        super.channelRead(ctx, msg);

    }

    @Override
    public void close() throws IOException {
        this.executorService.shutdown();
    }

    private String getErrorStack(Exception e) {
        String errorMessage = "";
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement element : elements) {
            errorMessage += element.toString() + "\n";
        }
        errorMessage += e.getLocalizedMessage();
        return errorMessage;
    }
}

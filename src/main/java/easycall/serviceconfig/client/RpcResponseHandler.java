package easycall.serviceconfig.client;

import easycall.codec.frame.Framer;
import easycall.codec.packet.NullObject;
import easycall.codec.packet.Packet;
import easycall.codec.packet.ResponsePacket;
import easycall.exception.DataDeSerializeException;
import easycall.exception.ResultCode;
import easycall.network.client.RpcResult;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理RPC请求返回的数据
 * @author 翁富鑫 2019/3/29 17:29
 */
public class RpcResponseHandler implements Runnable{

    private ByteBuf inputByteBuf;
    private RpcMessageManager rpcMessageManager;

    public RpcResponseHandler(ByteBuf inputByteBuf, RpcMessageManager rpcMessageManager) {
        this.inputByteBuf = inputByteBuf;
        this.rpcMessageManager = rpcMessageManager;
    }

    @Override
    public void run() {
        Exception error = null;
        RpcResult rpcResult = null;
        Packet packet = null;
        try {
            // 数据解码
            packet = Framer.decode(inputByteBuf);
            ReferenceCountUtil.release(inputByteBuf);
        } catch (DataDeSerializeException e) {
            e.printStackTrace();
            error = e;
        } catch (Exception e) {
            error = e;
            e.printStackTrace();
        }
        if (error != null) {
            // 本地发生了异常
            String errorMessage = getErrorStack(error);
            rpcResult = new RpcResult(packet.getRequestId(), String.class.getTypeName(), errorMessage, false);
        } else {
            // 本地没发生异常
            if (!(packet instanceof ResponsePacket)) {
                // 错误包，不做处理
            } else {
                try {
                    ResponsePacket responsePacket = (ResponsePacket) packet;
                    int resultCode = responsePacket.getResultCode();
                    List<String> resultTypeNames = responsePacket.getObjectTypeNames();
                    List<Object> resultObjects = responsePacket.getObjects();
                    // 返回的数据只有一个
                    String resultType = resultTypeNames.get(0);
                    Object resultObject = resultObjects.get(0);
                    if (resultObject instanceof NullObject) {
                        // 处理返回值为null

                        // 返回类型为封装的数据
                        resultType = ((NullObject) resultObject).getOriginalTypeName();
                        // 真正的返回数据为Null
                        resultObject = null;
                    }
                    if (ResultCode.SUCCESS.getCode() != resultCode) {
                        // 调用失败
                        rpcResult = new RpcResult(responsePacket.getRequestId(), resultType, resultObject, false);
                    } else {
                        rpcResult = new RpcResult(responsePacket.getRequestId(), resultType, resultObject, true);
                    }
                } catch (Exception e) {
                    String errorMessage = getErrorStack(e);
                    rpcResult = new RpcResult(packet.getRequestId(), String.class.getTypeName(), errorMessage, false);
                }
            }
        }
        this.rpcMessageManager.addRpcResult(packet.getRequestId(), rpcResult);
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

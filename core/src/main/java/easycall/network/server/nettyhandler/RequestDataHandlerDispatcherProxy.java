package easycall.network.server.nettyhandler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.MessageType;
import easycall.codec.packet.Packet;
import easycall.codec.packet.RequestPacket;
import easycall.codec.packet.ResponsePacket;
import easycall.codec.serializer.SerializeType;
import easycall.exception.*;
import easycall.initconfig.ServerInitializer;
import easycall.serviceconfig.server.RPCProvider;
import easycall.serviceconfig.server.RpcProviderManager;
import easycall.serviceconfig.server.serverhandler.HeartBeatHandler;
import easycall.serviceconfig.server.serverhandler.ServiceDataHandler;
import easycall.thread.ExecutorManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 真正的请求数据处理器
 * @author 翁富鑫 2019/3/25 11:22
 */
public class RequestDataHandlerDispatcherProxy {

    /**
     * 写数据的Channel
     */
    private Channel channel;

    /**
     * 请求的数据
     */
    private ByteBuf buf;

    /**
     * 执行管理器
     */
    private ExecutorManager executorManager;

    /**
     * RpcProvider的管理器
     */
    private RpcProviderManager rpcProviderManager;

    private ServerInitializer serverInitializer;

    public RequestDataHandlerDispatcherProxy(Channel channel, ByteBuf buf, ExecutorManager executorManager, RpcProviderManager rpcProviderManager, ServerInitializer serverInitializer) {
        this.channel = channel;
        this.buf = buf;
        this.executorManager = executorManager;
        this.rpcProviderManager = rpcProviderManager;
        this.serverInitializer = serverInitializer;
    }

    public void handle() {
        Packet packet = null;
        Exception error = null;
        ResultCode resultCode = ResultCode.SUCCESS;
        try {
            packet = Framer.decode(buf);
            if (packet instanceof RequestPacket) {
                RequestPacket requestPacket = (RequestPacket) packet;
                switch (packet.getMessageType()) {
                    case HEARTBEAT_REQUEST:
                        // 处理心跳
                        executorManager.submitTask(new HeartBeatHandler(channel , requestPacket, null , serverInitializer));
                        break;
                    case SERVICE_DATA_REQUEST:
                        // 处理业务请求
                        handleService(requestPacket);
                        break;
                }
            } else {
                throw new UnSupportedException("不支持的请求数据类型：" + packet.getMessageType().name());
            }
        } catch (MagicCheckException e) {
            // 魔数错误不做处理
            e.printStackTrace();
        } catch (UnSupportedException e) {
            resultCode = ResultCode.UN_SUPPORTED_ERROR;
            e.printStackTrace();
        } catch (DataSerializeException e) {
            resultCode = ResultCode.DE_SERIALIZE_ERROR;
            error = e;
            e.printStackTrace();
        } catch (DataDeSerializeException e) {
            resultCode = ResultCode.SERIALIZE_ERROR;
            error = e;
            e.printStackTrace();
        } catch (RpcServiceNotFoundException e) {
            resultCode = ResultCode.SERVICE_NOT_FOUNT_ERROR;
            error = e;
            e.printStackTrace();
        } catch (Exception e) {
            resultCode = ResultCode.UNKNOWN_ERROR;
            error = e;
            e.printStackTrace();
        }
        if (packet != null) {
            // 写回错误信息
            String errorMsg = getErrorStack(error);
            writeResult(errorMsg, resultCode.getCode(), packet.getRequestId());
        }
    }

    private void writeResult(String error , int resultCode, long requestId) {
        String classTypeName = error.getClass().getTypeName();
        List<String> transTypeNames = new ArrayList<>();
        transTypeNames.add(classTypeName);
        List<Object> transObjects = new ArrayList<>();
        transObjects.add(error);
        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.setSerializeType((SerializeType) serverInitializer.getInitProperties().get("serializeType"));
        responsePacket.setRequestId(requestId);
        responsePacket.setMessageType(MessageType.SERVICE_DATA_RESPONSE);
        responsePacket.setResultCode(resultCode);
        responsePacket.setTransObjectTypeNames(transTypeNames);
        responsePacket.setTransObjects(transObjects);
        try {
            channel.writeAndFlush(Framer.encode(responsePacket));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void handleService(RequestPacket requestPacket) throws RpcServiceNotFoundException {
        String serviceName = requestPacket.getTargetService();
        String version = requestPacket.getTargetVersion();
        RPCProvider rpcProvider = rpcProviderManager.getProviderByServiceName(serviceName , version);
        if (rpcProvider == null) {
            throw new RpcServiceNotFoundException("服务不存在");
        }
        if (rpcProvider.getExecutorService() != null) {
            // 数据处理的任务交由其专属线程执行
            rpcProvider.getExecutorService().submit(new ServiceDataHandler(channel , requestPacket, rpcProvider, serverInitializer));
        } else {
            // 交由公共的处理线程池进行处理
            executorManager.submitTask(new ServiceDataHandler(channel , requestPacket, rpcProvider, serverInitializer));
        }
    }

}

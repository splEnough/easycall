package easycall.network.server.RequestHandler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.MessageType;
import easycall.codec.packet.RequestPacket;
import easycall.codec.packet.ResponsePacket;
import easycall.codec.serializer.SerializeType;
import easycall.exception.ResultCode;
import easycall.config.ServerInitializer;
import easycall.serviceconfig.server.RPCProvider;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务数据处理器
 * @author 翁富鑫 2019/3/25 20:58
 */
public class ServiceDataHandler extends RequestHandlerBase {

    public ServiceDataHandler(Channel channel, RequestPacket packet, RPCProvider<?> rpcProvider, ServerInitializer serverInitializer) {
        super(channel, packet ,rpcProvider, serverInitializer);
    }

    @Override
    public void run() {
        Object proxyObject = rpcProvider.getProxyObject();
        // 要调用的方法名
        String methodName = requestPacket.getTargetMethod();
        // 调用的方法的参数类型
        List<String> typeNamesList = requestPacket.getObjectTypeNames();
        List<Class<?>> paramTypes = new ArrayList<>();
        try {
            for (String typeName : typeNamesList) {
                paramTypes.add(Class.forName(typeName));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 请求的参数
        List<Object> objects = requestPacket.getObjects();

        String errorMessage = "";
        Exception error = null;
        try {
            // TODO 验证类型转换结果
            Class[] clses = new Class[paramTypes.size()];
            for (int i = 0;i < paramTypes.size();i++) {
                clses[i] = paramTypes.get(i);
            }
            Method method = proxyObject.getClass().getMethod(methodName , clses);

            Object invokeResult = method.invoke(proxyObject , objects.toArray());
            writeResult(invokeResult , ResultCode.SUCCESS.getCode());
            return;
        } catch (IllegalAccessException e) {
            error = e;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            error = e;
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            error = e;
            e.printStackTrace();
        }
        StackTraceElement[] elements = error.getStackTrace();
        for (StackTraceElement element : elements) {
            errorMessage += element.toString() + "\n";
        }
        errorMessage += error.getLocalizedMessage();
        writeResult(errorMessage , ResultCode.UNKNOWN_ERROR.getCode());
    }

    private void writeResult(Object invokeResult, int resultCode) {
        String classTypeName = invokeResult.getClass().getTypeName();
        List<String> transTypeNames = new ArrayList<>();
        transTypeNames.add(classTypeName);
        List<Object> transObjects = new ArrayList<>();
        transObjects.add(invokeResult);
        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.setSerializeType((SerializeType) serverInitializer.getInitProperties().get("serializeType"));
        responsePacket.setRequestId(requestPacket.getRequestId());
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
}


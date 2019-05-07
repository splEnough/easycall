package easycall.serviceconfig.server.serverhandler;

import easycall.Util.StringUtil;
import easycall.codec.frame.Framer;
import easycall.codec.packet.MessageType;
import easycall.codec.packet.NullObject;
import easycall.codec.packet.RequestPacket;
import easycall.codec.packet.ResponsePacket;
import easycall.exception.DataSerializeException;
import easycall.exception.ResultCode;
import easycall.initconfig.ServerParam;
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

    public ServiceDataHandler(Channel channel, RequestPacket packet, RPCProvider<?> rpcProvider, ServerParam serverParam) {
        super(channel, packet ,rpcProvider, serverParam);
    }

    @Override
    public void run() {
        Object proxyObject = rpcProvider.getProxyObject();
        // 要调用的方法名
        String methodName = requestPacket.getTargetMethod();
        // 调用的方法的参数类型
        List<String> typeNamesList = requestPacket.getObjectTypeNames();
        // 请求的参数
        List<Object> objects = requestPacket.getObjects();

        List<Class<?>> paramTypes = new ArrayList<>();
        try {
            for (int i = 0;i < typeNamesList.size();i++) {
                if (StringUtil.equals(typeNamesList.get(i), NullObject.class.getTypeName())) {
                    // 空对象占位，需要解析其中的真实类型
                    String originalName = ((NullObject)objects.get(i)).getOriginalTypeName();
                    paramTypes.add(Class.forName(originalName));
                    // 真实的数据为null
                    objects.set(i,null);
                } else {
                    paramTypes.add(Class.forName(typeNamesList.get(i)));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String errorMessage = "";
        Exception error = null;
        try {
            Class[] clses = new Class[paramTypes.size()];
            for (int i = 0;i < paramTypes.size();i++) {
                clses[i] = paramTypes.get(i);
            }
            Method method = proxyObject.getClass().getMethod(methodName , clses);

            Object invokeResult = method.invoke(proxyObject , objects.toArray());
            // 方法的返回类型
            Class<?> returnType = method.getReturnType();
            if (invokeResult == null) {
                // 处理空值返回
                invokeResult = new NullObject(returnType.getTypeName());
            }
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
        // 异常栈
        errorMessage = getErrorStack(error);
        writeResult(errorMessage , ResultCode.UNKNOWN_ERROR.getCode());
    }

    /**
     * 写返回数据，如果本地出现了错误，那么返回的数据就是异常栈
     * @param invokeResult
     * @param resultCode
     */
    private void writeResult(Object invokeResult, int resultCode) {
        String classTypeName = invokeResult.getClass().getTypeName();
        List<String> transTypeNames = new ArrayList<>();
        transTypeNames.add(classTypeName);
        List<Object> transObjects = new ArrayList<>();
        transObjects.add(invokeResult);
        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.setSerializeType(serverParam.getSerializeType());
        responsePacket.setRequestId(requestPacket.getRequestId());
        responsePacket.setMessageType(MessageType.SERVICE_DATA_RESPONSE);
        responsePacket.setResultCode(resultCode);
        responsePacket.setTransObjectTypeNames(transTypeNames);
        responsePacket.setTransObjects(transObjects);
        try {
            channel.writeAndFlush(Framer.encode(responsePacket));
        } catch (DataSerializeException e) {
            responsePacket.setResultCode(ResultCode.SERIALIZE_ERROR.getCode());
            transObjects.clear();
            transObjects.add("服务端序列化数据失败");
            transTypeNames.clear();
            transTypeNames.add(String.class.getTypeName());
            try {
                channel.writeAndFlush(Framer.encode(responsePacket));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (Exception e) {
            responsePacket.setResultCode(ResultCode.SERIALIZE_ERROR.getCode());
            transObjects.clear();
            String errorMsg = getErrorStack(e);
            transObjects.add(errorMsg);
            transTypeNames.clear();
            transTypeNames.add(String.class.getTypeName());
            try {
                channel.writeAndFlush(Framer.encode(responsePacket));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private String getErrorStack(Exception error) {
        String errorMessage = "";
        // 异常栈
        StackTraceElement[] elements = error.getStackTrace();
        for (StackTraceElement element : elements) {
            errorMessage += element.toString() + "\n";
        }
        errorMessage += error.getLocalizedMessage();
        return errorMessage;
    }
}


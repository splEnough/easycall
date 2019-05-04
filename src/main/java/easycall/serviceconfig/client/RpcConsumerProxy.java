package easycall.serviceconfig.client;

import easycall.codec.packet.MessageType;
import easycall.codec.packet.NullObject;
import easycall.codec.packet.RequestPacket;
import easycall.codec.serializer.SerializeType;
import easycall.initconfig.ClientInitializer;
import easycall.exception.DataSerializeException;
import easycall.exception.RpcCallResponseTimeOutException;
import easycall.network.client.Connection;
import easycall.network.client.ConnectionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通过 DefaultRpcConsumerProxyContainer类获取
 * RPC服务处理代理类，针对某个接口进行接口实例的动态生成
 * @author 翁富鑫 2019/3/28 10:03
 */
public class RpcConsumerProxy implements InvocationHandler {

    private Class<?> serviceType;
    private Connection rpcConnection;
    private String targetService;
    private String targetVersion;
    private long rpcTimeout;
    private ConnectionFactory connectionFactory;
    private ClientInitializer clientInitializer;
    private RpcMessageManager rpcMessageManager;

    public RpcConsumerProxy(ConnectionFactory connectionFactory, String targetService, String targetVersion, ClientInitializer clientInitializer, RpcMessageManager rpcMessageManager, long rpcTimeout, Class<?> classType) {
        this.connectionFactory = connectionFactory;
        this.targetService = targetService;
        this.targetVersion = targetVersion;
        this.clientInitializer = clientInitializer;
        this.rpcMessageManager = rpcMessageManager;
        this.serviceType = classType;
        this.rpcTimeout = rpcTimeout;
    }

    /**
     * @throws RpcCallResponseTimeOutException 响应数据超时
     * @throws DataSerializeException RPC请求数据序列化异常
     * @throws InterruptedException 中断异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (rpcConnection == null || rpcConnection.isClose()) {
            // 重新建立连接
            rpcConnection = this.connectionFactory.buildTargetServiceConnection(targetService , targetVersion, (Integer) clientInitializer.getInitialParam("port"), 5, TimeUnit.SECONDS);
        }
        Class<?>[] methodParameterTypes = method.getParameterTypes();
        RequestPacket requestPacket = packageRequestParam(methodParameterTypes, args , method.getName());
        Object rpcResult = rpcMessageManager.sendRequest(rpcConnection.getConnectionChannel(), requestPacket);
        return rpcResult;
    }

    private RequestPacket packageRequestParam(Class<?>[] methodParameterTypes, Object[] args, String methodName) {
        RequestPacket requestPacket = new RequestPacket();
        List<String> requestParamTypeNames = new ArrayList<>();
        List<Object> transObjects = new ArrayList<>();
        for (int i = 0 ;i < args.length;i++) {
            if (args[i] == null) {
                // 参数为null，封装一个空对象
                NullObject nullObject = new NullObject(methodParameterTypes[i].getTypeName());
                transObjects.add(nullObject);
                requestParamTypeNames.add(nullObject.getClass().getTypeName());
            } else {
                requestParamTypeNames.add(methodParameterTypes[i].getTypeName());
                transObjects.add(args[i]);
            }
        }
        requestPacket.setTargetService(this.targetService);
        requestPacket.setTargetVersion(this.targetVersion);
        requestPacket.setMessageType(MessageType.SERVICE_DATA_REQUEST);
        requestPacket.setTransObjects(transObjects);
        requestPacket.setTransObjectTypeNames(requestParamTypeNames);
        requestPacket.setTargetMethod(methodName);
        requestPacket.setTimeout(rpcTimeout);
        requestPacket.setSerializeType((SerializeType) clientInitializer.getInitialParam("serializeType"));
        return requestPacket;
    }

}

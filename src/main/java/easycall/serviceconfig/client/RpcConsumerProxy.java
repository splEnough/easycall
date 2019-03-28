package easycall.serviceconfig.client;

import easycall.codec.packet.MessageType;
import easycall.codec.packet.RequestPacket;
import easycall.codec.serializer.SerializeType;
import easycall.config.ClientInitializer;
import easycall.exception.DataSerializeException;
import easycall.exception.RpcCallResponseTimeOutException;
import easycall.network.common.connection.Connection;
import easycall.network.common.connection.ConnectionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RPC服务处理代理类，针对某个接口进行接口实例的动态生成
 * @author 翁富鑫 2019/3/28 10:03
 */
public class RpcConsumerProxy implements InvocationHandler {

    private Connection rpcConnection;
    private String targetService;
    private String targetVersion;
    private long rpcTimeout;
    private ConnectionFactory connectionFactory;
    private ClientInitializer clientInitializer;
    private RpcMessageManager rpcMessageManager;

    public RpcConsumerProxy(ConnectionFactory connectionFactory, String targetService, String targetVersion, ClientInitializer clientInitializer, RpcMessageManager rpcMessageManager, long rpcTimeout) {
        this.connectionFactory = connectionFactory;
        this.targetService = targetService;
        this.targetVersion = targetVersion;
        this.clientInitializer = clientInitializer;
        this.rpcMessageManager = rpcMessageManager;
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
        RequestPacket requestPacket = packageRequestParam(args , method.getName());
        Object rpcResult = rpcMessageManager.sendRequest(rpcConnection.getConnectionChannel(), requestPacket);
        return rpcResult;
    }

    private RequestPacket packageRequestParam(Object[] args, String methodName) {
        RequestPacket requestPacket = new RequestPacket();
        List<String> requestParamTypeNames = new ArrayList<>();
        for (Object obj : args) {
            requestParamTypeNames.add(obj.getClass().getTypeName());
        }
        List<Object> transObjects = new ArrayList<>(Arrays.asList(args));
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

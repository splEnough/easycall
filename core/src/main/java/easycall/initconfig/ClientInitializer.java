package easycall.initconfig;

import easycall.codec.serializer.SerializeType;
import easycall.loadbalance.LoadBalanceType;
import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端启动加载器
 *
 * @author 翁富鑫 2019/3/28 10:33
 */
public class ClientInitializer {

    private Map<String, Object> initProperties = new HashMap<>();

    public ClientInitializer() {
        init();
    }

    private void init() {
        // 默认的端口号
        Integer port = 8888;
        String portString = System.getProperty("easycall.server.port");
        String serializeType = System.getProperty("easycall.serialize.type.name");
        if (!StringUtil.isNullOrEmpty(portString)) {
            try {
                port = Integer.parseInt(portString);
            } catch (Exception e) {
                throw e;
            }
        }

        // 负载均衡
        Integer loadBalanceTypeInt = 0;
        String typeString = System.getProperty("easycall.loadbalance.type");
        if (!StringUtil.isNullOrEmpty(typeString)) {
            try {
                loadBalanceTypeInt = Integer.parseInt(typeString);
            } catch (Exception e) {
                throw e;
            }
        }
        LoadBalanceType loadBalanceType = LoadBalanceType.getTypeByCode(loadBalanceTypeInt);


        // TODO 默认的端口绑定超时时间


        // 默认的serviceVersion
        String version = System.getProperty("easycall.rpc.service.version");
        if (StringUtil.isNullOrEmpty(version)) {
            // 默认为1.0版本
            version = "1.0";
        }

        // 默认3秒的RPC调用超时
        long rpcTimeout = 3000;
        String timeoutString = System.getProperty("easycall.rpc.timeout");
        if (!StringUtil.isNullOrEmpty(timeoutString)) {
            try {
                rpcTimeout = Integer.parseInt(timeoutString);
            } catch (Exception e) {
                throw e;
            }
        }


        // 序列化类型
        SerializeType defaultSerializeType = SerializeType.PROTO_STUFF;
        if (!StringUtil.isNullOrEmpty(serializeType)) {
            switch (serializeType) {
                case "protostuff":
                    defaultSerializeType = SerializeType.PROTO_STUFF;
                    break;
                case "kryo":
                    defaultSerializeType = SerializeType.KRYO;
                    break;
                case "jdk":
                    defaultSerializeType = SerializeType.JDK;
                    break;
            }
        }
        initProperties.put("serializeType", defaultSerializeType);
        initProperties.put("port", port);
        initProperties.put("loadBalanceType", loadBalanceType);
        initProperties.put("rpcTimeout", rpcTimeout);
        initProperties.put("version", version);
    }

    public Map<String, Object> getInitProperties() {
        return initProperties;
    }

    public Object getInitialParam(String key) {
        return initProperties.get(key);
    }
}

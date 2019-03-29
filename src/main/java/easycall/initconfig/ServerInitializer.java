package easycall.config;

import easycall.codec.serializer.SerializeType;
import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 服务端启动加载器，主要是对运行的必要参数的检测和加载
 * @author 翁富鑫 2019/3/2 20:03
 */
public class ServerInitializer {

    private Map<String, Object> initProperties = new HashMap<>();

    public ServerInitializer() {
        init();
    }

    private void init() {
        // 默认的端口号
        Integer port = 8888;
        String portString = System.getProperty("easycall.server.port");
        String serializeType = System.getProperty("serialize.type.name");
        if (!StringUtil.isNullOrEmpty(portString)) {
            try {
                port = Integer.parseInt(portString);
            } catch (Exception e) {
                throw e;
            }
        }

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
        initProperties.put("serializeType" , defaultSerializeType);
        initProperties.put("port", port);
    }

    public Map<String, Object> getInitProperties() {
        return initProperties;
    }
}

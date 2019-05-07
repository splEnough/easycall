package easycall.initconfig;

import easycall.codec.serializer.SerializeType;
import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务端启动加载器，主要是对运行的必要参数的检测和加载
 * @author 翁富鑫 2019/3/2 20:03
 */
public class ServerInitializer {

    public ServerInitializer() {
        init();
    }

    private Integer port = 8888;
    private SerializeType serializeType = SerializeType.PROTO_STUFF;
    private String connString;
    // 服务的版本号，统一设置
    private String version;

    private void init() {
        // 默认的端口号
        String portString = System.getProperty("easycall.provider.port");
        String serializeType = System.getProperty("easycall.provider.type");
        String connString = System.getProperty("easycall.provider.connString");
        String version = System.getProperty("easycall.provider.version");
        if (!StringUtil.isNullOrEmpty(portString)) {
            try {
                this.port = Integer.parseInt(portString);
            } catch (Exception e) {
                throw e;
            }
        }
        this.serializeType = getSerializeType(serializeType);
        this.connString = connString;
        this.version = version;
    }

    public SerializeType getSerializeType(String serializeType) {
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
        return defaultSerializeType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(String serializeType) {
        this.serializeType = getSerializeType(serializeType);
    }

    public String getConnString() {
        return connString;
    }

    public void setConnString(String connString) {
        this.connString = connString;
    }
}

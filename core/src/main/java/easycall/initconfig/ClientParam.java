package easycall.initconfig;

import easycall.codec.serializer.SerializeType;
import easycall.loadbalance.LoadBalanceType;
import io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端参数
 * @author 翁富鑫 2019/3/28 10:33
 */
public class ClientParam {

    public ClientParam() {
        init();
    }

    private Integer port = 8888;
    private SerializeType serializeType = SerializeType.PROTO_STUFF;
    private String connString;
    // 服务的版本号，统一设置
    private String version = "1.0";
    // 默认随机选择
    private LoadBalanceType loadBalanceType = LoadBalanceType.RANDOM;
    // 超时时间毫秒
    private Long rpcTimeout = 5000L;

    private void init() {
        // 默认的端口号
        String portString = System.getProperty("easycall.consumer.port");
        String serializeType = System.getProperty("easycall.consumer.serializeType");
        String connString = System.getProperty("easycall.consumer.connString");
        String version = System.getProperty("easycall.consumer.version");
        String rpcTimeout = System.getProperty("easycall.consumer.rpcTimeout");
        // 数字
        String loadBalanceType = System.getProperty("easycall.consumer.loadBalanceType");
        if (!StringUtil.isNullOrEmpty(portString)) {
            try {
                this.port = Integer.parseInt(portString);
            } catch (Exception e) {
                throw e;
            }
        }
        this.serializeType = getSerializeType(serializeType);
        this.connString = (connString == null)? this.connString : connString;
        this.version = (version == null)? this.version : version;
        if (!StringUtil.isNullOrEmpty(loadBalanceType)) {
            this.loadBalanceType = LoadBalanceType.getTypeByCode(Integer.parseInt(loadBalanceType));
        }
        this.rpcTimeout = (rpcTimeout == null)? this.rpcTimeout : Long.parseLong(rpcTimeout);
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

    public LoadBalanceType getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(LoadBalanceType loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }

    public void setLoadBalanceType(Integer loadBalanceType) {
        this.loadBalanceType = LoadBalanceType.getTypeByCode(loadBalanceType);
    }

    public Long getRpcTimeout() {
        return rpcTimeout;
    }

    public void setRpcTimeout(Long rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }
}

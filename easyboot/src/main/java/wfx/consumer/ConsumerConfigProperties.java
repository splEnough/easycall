package wfx.consumer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 翁富鑫 2019/4/29 22:16
 */
@ConfigurationProperties(prefix = "easycall.consumer")
public class ConsumerConfigProperties {

    // zookeeper的连接字符串
    private String connString;
    // 连接的服务端端口
    private String port;
    private String loadBalanceType;
    private String serializeType;
    // 服务的版本号，统一设置
    private String version;
    // RPC服务调用超时时间
    private String rpcTimeout;

    public ConsumerConfigProperties() {
        System.out.println("ConsumerConfigProperties  --- init() ");
    }

    public String getConnString() {
        return connString;
    }

    public void setConnString(String connString) {
        this.connString = connString;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(String loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }

    public String getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRpcTimeout() {
        return rpcTimeout;
    }

    public void setRpcTimeout(String rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }
}

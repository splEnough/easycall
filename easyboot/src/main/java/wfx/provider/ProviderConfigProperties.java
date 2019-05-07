package wfx.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 翁富鑫 2019/5/5 21:20
 */
@ConfigurationProperties(prefix = "easycall.provider")
public class ProviderConfigProperties {
    private String connString;
    // RPC服务监听端口
    private String port;
    // 序列化类型
    private String serialize;

    public ProviderConfigProperties() {
        System.out.println("ProviderConfigProperties --- init()");
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getConnString() {
        return connString;
    }

    public void setConnString(String connString) {
        this.connString = connString;
    }
}

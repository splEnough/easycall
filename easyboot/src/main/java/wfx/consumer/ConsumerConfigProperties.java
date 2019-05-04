package wfx.consumer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 翁富鑫 2019/4/29 22:16
 */
@ConfigurationProperties(prefix = "easycall.consumer")
public class ConsumerConfigProperties {
    // 是否启动
    private String enabled;
    // zookeeper的连接字符串
    private String connString;

    public String getConnString() {
        return connString;
    }

    public void setConnString(String connString) {
        this.connString = connString;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}

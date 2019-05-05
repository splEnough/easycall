package wfx.consumer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 翁富鑫 2019/4/29 22:16
 */
@ConfigurationProperties(prefix = "easycall.consumer")
public class ConsumerConfigProperties {

    // zookeeper的连接字符串
    private String connString;

    public ConsumerConfigProperties() {
        System.out.println("ConsumerConfigProperties  --- init() ");
    }

    public String getConnString() {
        return connString;
    }

    public void setConnString(String connString) {
        System.out.println("设置值");
        this.connString = connString;
    }

}

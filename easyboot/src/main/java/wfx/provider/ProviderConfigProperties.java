package wfx.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 翁富鑫 2019/5/5 21:20
 */
@ConfigurationProperties(prefix = "easycall.provider")
public class ProviderConfigProperties {
    private String connString;

    public ProviderConfigProperties() {
        System.out.println("ProviderConfigProperties --- init()");
    }

    public String getConnString() {
        return connString;
    }

    public void setConnString(String connString) {
        this.connString = connString;
    }
}

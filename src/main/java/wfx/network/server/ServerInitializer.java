package wfx.network.server;

import io.netty.util.internal.StringUtil;

import java.util.Properties;

/**
 * 服务端启动加载器，主要是对运行的必要参数的检测和加载
 * @author 翁富鑫 2019/3/2 20:03
 */
public class ServerInitializer {

    private Properties initProperties = new Properties();

    public ServerInitializer() {
        init();
    }

    private void init() {
        // 默认的端口号
        Integer port = 8888;
        String portString = System.getProperty("easycall.server.port");
        if (!StringUtil.isNullOrEmpty(portString)) {
            try {
                port = Integer.parseInt(portString);
            } catch (Exception e) {
                throw e;
            }
        }
        initProperties.put("port", port.toString());
    }

    public Properties getInitProperties() {
        return initProperties;
    }
}

package easycall.network.server.starter;

import java.io.Closeable;

/**
 * 服务端启动器
 * @author 翁富鑫 2019/3/3 9:54
 */
public interface ServerStarter extends Closeable {

    /**
     * 进行服务端的启动
     * @return 服务端Channel
     */
    void start() throws Exception ;

}

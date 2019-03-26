package easycall.network.server.test;

import easycall.network.server.DefaultNioServerStarter;
import easycall.network.server.ServerInitializer;
import easycall.network.server.ServerStarter;
import easycall.thread.DefaultExecutorManager;
import easycall.thread.ExecutorManager;

/**
 * @author 翁富鑫 2019/3/3 11:16
 */
public class ServerTest {
    public static void main(String[] args) throws Exception {
        ServerInitializer initializer = new ServerInitializer();
        ExecutorManager executorManager = new DefaultExecutorManager();
        ServerStarter serverStarter = new DefaultNioServerStarter(initializer, executorManager);
        serverStarter.start();
//        TimeUnit.SECONDS.sleep(20);
//        serverStarter.close();
//        executorManager.close();
    }
}

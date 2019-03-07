package wfx.network.server.test;

import wfx.network.server.DefaultNioServerStarter;
import wfx.network.server.ServerInitializer;
import wfx.network.server.ServerStarter;

import java.util.concurrent.TimeUnit;

/**
 * @author 翁富鑫 2019/3/3 11:16
 */
public class ServerTest {
    public static void main(String[] args) throws Exception {
        ServerInitializer initializer = new ServerInitializer();
        ServerStarter serverStarter = new DefaultNioServerStarter(initializer);
        serverStarter.start();
        TimeUnit.SECONDS.sleep(20);
        serverStarter.close();
    }
}

package easycall.thread;

import io.netty.channel.Channel;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 默认的执行管理者
 * @author 翁富鑫 2019/3/25 17:10
 */
public class DefaultExecutorManager implements ExecutorManager {

    /**
     * RpcService公用线程池参数
     * ------------------------------------------
     */
    private int corePoolSize = 50;
    private int maxPoolSize = 640;
    private long keepAliveTime = 0L;
    /**
     * 有限的工作队列，默认只保存50个工作任务
     */
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);

    /**
     * -------------------------------------------
     */

    private ExecutorService publicRpcServiceExecutorService ;

    public DefaultExecutorManager() {
        init();
    }

    private void init() {
        publicRpcServiceExecutorService = new PublicExecutorService();
    }

    @Override
    public void submitTask(Runnable runnable) throws RejectedExecutionException{
        publicRpcServiceExecutorService.submit(runnable);
    }

    @Override
    public Executor allocatePrivateExecutor(Integer corePoolSize, Integer maxPoolSize, String name) {
        return null;
    }

    @Override
    public void close() throws IOException {
        this.publicRpcServiceExecutorService.shutdown();
    }

    private class PublicExecutorService extends ThreadPoolExecutor {

        public PublicExecutorService() {
            super(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MICROSECONDS, workQueue);
        }
    }
}

package easycall.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 默认的执行管理者
 *
 * @author 翁富鑫 2019/3/25 17:10
 */
public class DefaultExecutorManager implements ExecutorManager {

    /**
     * RpcService公用线程池参数
     * ------------------------------------------
     */
    /**
     * 核心线程数量
     */
    private int corePoolSize = 5;
    /**
     * 最大线程数量
     */
    private int maxPoolSize = 5;
    /**
     * 空闲保活时间（ms）
     */
    private long keepAliveTime = 0L;
    /**
     * 有限的工作队列，默认只保存50个工作任务
     */
//    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
    // 使用马上交付队列，不缓存任务
    private BlockingQueue<Runnable> workQueue = new SynchronousQueue<>(true);

    private class PublicExecutorService extends ThreadPoolExecutor {

        public PublicExecutorService() {
            super(corePoolSize, maxPoolSize, keepAliveTime
                    , TimeUnit.MICROSECONDS, workQueue, new DumpHandler());
        }

    }
    /**
     * -------------------------------------------
     */

    private ExecutorService publicRpcServiceExecutorService;

    /**
     * RPCProvider特有的ExecutorService
     */
    private List<ExecutorService> providerExecutorServices = new ArrayList<>();

    public DefaultExecutorManager() {
        init();
    }

    private void init() {
        publicRpcServiceExecutorService = new PublicExecutorService();
    }

    @Override
    public void submitTask(Runnable runnable) throws RejectedExecutionException {
        publicRpcServiceExecutorService.submit(runnable);
    }

    @Override
    public ExecutorService allocatePrivateExecutor(Integer corePoolSize, Integer maxPoolSize, String name) {
        ExecutorService executorService = new PrivateExecutorService(corePoolSize, maxPoolSize);
        providerExecutorServices.add(executorService);
        return executorService;
    }

    @Override
    public void close() throws IOException {
        // 释放公共的线程池
        this.publicRpcServiceExecutorService.shutdown();
        // 释放每个服务提供者私有的线程
        if (this.providerExecutorServices != null) {
            for (ExecutorService executorService : providerExecutorServices) {
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            }
        }
    }

    private class PrivateExecutorService extends ThreadPoolExecutor {

        public PrivateExecutorService(int corePoolSize, int maximumPoolSize) {
            super(corePoolSize, maximumPoolSize, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        }
    }
}

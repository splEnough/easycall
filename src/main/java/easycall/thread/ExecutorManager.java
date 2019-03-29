package easycall.thread;


import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * 线程管理器
 * @author 翁富鑫 2019/3/25 11:03
 */
public interface ExecutorManager extends Closeable {

    /**
     * 提交一个任务，直接返回
     * TODO 当任务拒绝执行时，dump内存
     * @param runnable 要执行的任务
     * @return
     */
    void submitTask(Runnable runnable) throws RejectedExecutionException;

    /**
     * 分配一个专属的线程池
     * @param corePoolSize
     * @param maxPoolSize
     * @param name
     * @return
     */
    ExecutorService allocatePrivateExecutor(Integer corePoolSize , Integer maxPoolSize, String name) ;

}

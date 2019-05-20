package easycall.thread;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sixie.xyn on 2017/1/22.
 */
public class DumpHandler implements RejectedExecutionHandler {

    /**
     * 10分钟内触发了，不会进行打印
     */
    private static final long DELTA_MILLIS = TimeUnit.MINUTES.toMillis(10);
    /**
     * 进入限制
     */
    private AtomicBoolean hasDump = new AtomicBoolean();
    /**
     * 门禁时间，当前时间超过该时间才可以进行
     */
    private long threshold = 0;

    /**
     * Creates a <tt>CallerRunsPolicy</tt>.
     */
    public DumpHandler() {
    }

    /**
     * Executes task r in the caller's thread, unless the executor has been
     * shut down, in which case the task is discarded.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        dumpJVMInfo();
        // ignore it when thread full and it will be timeout response for
        // client
//        throw new RejectedExecutionException();
    }

    private void dumpJVMInfo() {
        // It is ok even if two or more jstack file is out
        // 没有限制，超过阈值
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        String dateStr = sdf.format(new Date());
        String logPath = "D:\\resources\\learn\\"  + dateStr;
        FileOutputStream jstackStream = null;
//        FileOutputStream memoryStream = null;
        try {
            jstackStream = new FileOutputStream(logPath);
//            memoryStream = new FileOutputStream(new File(logPath, "HSF_Memory.log" + "." + dateStr));
            JvmUtil.jstack(jstackStream);
            System.out.println("dump了文件，路径为：" + logPath);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                jstackStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


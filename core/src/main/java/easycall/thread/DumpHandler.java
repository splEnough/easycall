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

public class DumpHandler implements RejectedExecutionHandler {

    public DumpHandler() {
    }

    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        dumpJVMInfo();
    }

    private void dumpJVMInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        String dateStr = sdf.format(new Date());
        String logPath = "D:" + File.separator + "resources" + File.separator + "learn" + File.separator  + dateStr;
        FileOutputStream jstackStream = null;
        try {
            jstackStream = new FileOutputStream(logPath);
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


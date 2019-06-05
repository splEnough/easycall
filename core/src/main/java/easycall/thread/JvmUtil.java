package easycall.thread;


import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.OutputStream;
import java.lang.management.*;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Set;

/**
 * JVM工具类，提供jstack jmap memory等信息的输出
 */
@SuppressWarnings("restriction")
public class JvmUtil {

    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
    private static volatile HotSpotDiagnosticMXBean hotspotMBean;
    private static volatile MemoryMXBean memoryMBean;

    private static Object lock = new Object();

    public static void jMap(String fileName, boolean live) throws Exception {
        initHotspotMBean();
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
        hotspotMBean.dumpHeap(fileName, live);
    }

    /**
     * jstack 输出线程信息
     *
     * @param stream 输出流
     * @throws Exception 异常
     */
    public static void jstack(OutputStream stream) throws Exception {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(allThreadIds, true, true);
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo != null) {
                stream.write(printThreadInfoDepth(threadInfo, 1000).getBytes());
            }
        }
    }

    /**
     * <pre>
     * 打印ThreadInfo，使用给定的深度
     * {@link ThreadInfo#toString()}
     *
     * </pre>
     * @param threadInfo 线程信息
     * @param depth      深度
     * @return 堆栈输出文案
     */
    static String printThreadInfoDepth(ThreadInfo threadInfo, int depth) {
        StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" +
                " Id=" + threadInfo.getThreadId() + " " +
                threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            sb.append(" on " + threadInfo.getLockName());
        }
        if (threadInfo.getLockOwnerName() != null) {
            sb.append(" owned by \"" + threadInfo.getLockOwnerName() +
                    "\" Id=" + threadInfo.getLockOwnerId());
        }
        if (threadInfo.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (threadInfo.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        int i = 0;
        for (; i < threadInfo.getStackTrace().length && i < depth; i++) {
            StackTraceElement ste = threadInfo.getStackTrace()[i];
            sb.append("\tat " + ste.toString());
            sb.append('\n');
            if (i == 0 && threadInfo.getLockInfo() != null) {
                Thread.State ts = threadInfo.getThreadState();
                switch (ts) {
                    case BLOCKED:
                        sb.append("\t-  blocked on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case TIMED_WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    default:
                }
            }

            for (MonitorInfo mi : threadInfo.getLockedMonitors()) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked " + mi);
                    sb.append('\n');
                }
            }
        }
        if (i < threadInfo.getStackTrace().length) {
            sb.append("\t...");
            sb.append('\n');
        }

        LockInfo[] locks = threadInfo.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            for (LockInfo li : locks) {
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * 内存使用
     *
     * @param stream 输出流
     * @return 输出比例
     * @throws Exception
     */
    public static double memoryUsed(OutputStream stream) throws Exception {
        initMemoryMBean();
        stream.write(
                "**********************************Memory Used**********************************\n".getBytes());
        String heapMemoryUsed = memoryMBean.getHeapMemoryUsage().toString() + "\n";
        stream.write(("Heap Memory Used: " + heapMemoryUsed).getBytes());
        String nonHeapMemoryUsed = memoryMBean.getNonHeapMemoryUsage().toString() + "\n";
        stream.write(("NonHeap Memory Used: " + nonHeapMemoryUsed).getBytes());

        return (double) (memoryMBean.getHeapMemoryUsage().getUsed()) / memoryMBean.getHeapMemoryUsage().getMax();

    }

    private static HotSpotDiagnosticMXBean getHotspotMBean() throws Exception {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<HotSpotDiagnosticMXBean>() {
                public HotSpotDiagnosticMXBean run() throws Exception {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    Set<ObjectName> s = server.queryNames(new ObjectName(HOTSPOT_BEAN_NAME), null);
                    Iterator<ObjectName> itr = s.iterator();
                    if (itr.hasNext()) {
                        ObjectName name = itr.next();
                        HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                                name.toString(), HotSpotDiagnosticMXBean.class);
                        return bean;
                    } else {
                        return null;
                    }
                }
            });
        } catch (Exception exp) {
            throw exp;
        }
    }

    private static MemoryMXBean getMemoryMBean() throws Exception {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<MemoryMXBean>() {
                public MemoryMXBean run() throws Exception {
                    return ManagementFactory.getMemoryMXBean();
                }
            });
        } catch (Exception exp) {
            throw exp;
        }
    }

    private static void initHotspotMBean() throws Exception {
        if (hotspotMBean == null) {
            synchronized (lock) {
                if (hotspotMBean == null) {
                    hotspotMBean = getHotspotMBean();
                }
            }
        }
    }

    private static void initMemoryMBean() throws Exception {
        if (memoryMBean == null) {
            synchronized (lock) {
                if (memoryMBean == null) {
                    memoryMBean = getMemoryMBean();
                }
            }
        }
    }
}

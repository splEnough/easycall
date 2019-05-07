package wfx.provider;

import easycall.initconfig.ServerParam;
import easycall.network.server.starter.ServerStarter;
import easycall.registercenter.server.Register;
import easycall.serviceconfig.server.RPCProvider;
import easycall.serviceconfig.server.RpcProviderManager;
import easycall.thread.ExecutorManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author 翁富鑫 2019/5/5 21:06
 */
public class ProviderSpringBean<T> implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private RPCProvider<T> rpcProvider;
    /**
     * RpcProvider的管理器
     */
    private RpcProviderManager rpcProviderManager;

    /**
     * 实现类Bean
     */
    private T targetServiceImpl;

    private String targetBeanName;

    private String version;

    private Integer threadPoolSize;

    private String proxyBeanName;

    private ApplicationContext applicationContext;

    public T getTargetServiceImpl() {
        return targetServiceImpl;
    }

    public void setTargetServiceImpl(T targetServiceImpl) {
        this.targetServiceImpl = targetServiceImpl;
    }

    public RPCProvider<?> getRpcProvider() {
        return rpcProvider;
    }

    public RpcProviderManager getRpcProviderManager() {
        return rpcProviderManager;
    }

    public void setRpcProviderManager(RpcProviderManager rpcProviderManager) {
        this.rpcProviderManager = rpcProviderManager;
    }

    public void setRpcProvider(RPCProvider<T> rpcProvider) {
        this.rpcProvider = rpcProvider;
    }

    public String getTargetBeanName() {
        return targetBeanName;
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public String getProxyBeanName() {
        return proxyBeanName;
    }

    public void setProxyBeanName(String proxyBeanName) {
        this.proxyBeanName = proxyBeanName;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 启动RPC端口监听
        try {
            ((ServerStarter)applicationContext.getBean("serverStarter")).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 启动注册器
        ((Register)applicationContext.getBean("register")).start();
        rpcProvider.setRpcServiceObject(targetServiceImpl);
        // 对外发布服务
        rpcProviderManager.exportProvider(rpcProvider);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.out.println("targetBeanName:" + targetBeanName);
        this.targetServiceImpl = (T)applicationContext.getBean(targetBeanName);
        this.rpcProviderManager = (RpcProviderManager) applicationContext.getBean("rpcProviderManager");
        if (this.version == null) {
            version = ((ServerParam)applicationContext.getBean("serverInitializer")).getVersion();
            rpcProvider.setVersion(version);
        }
        // 线程池处理
        if (threadPoolSize > 0) {
            rpcProvider.setExecutorService(((ExecutorManager)applicationContext.getBean("executorManager")).allocatePrivateExecutor(threadPoolSize, threadPoolSize, proxyBeanName));
        }
    }
}

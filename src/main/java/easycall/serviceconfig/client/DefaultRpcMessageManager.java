package easycall.serviceconfig.client;

import easycall.codec.frame.Framer;
import easycall.codec.packet.RequestPacket;
import easycall.exception.DataSerializeException;
import easycall.exception.RpcCallResponseTimeOutException;
import easycall.network.client.RpcResult;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * 默认的消息管理器
 * @author 翁富鑫 2019/3/28 13:11
 */
public class DefaultRpcMessageManager implements RpcMessageManager {

    /**
     * 请求id生成器
     */
    private AtomicLong requestIdGenerator = new AtomicLong(0);

    /**
     * 保存RPC请求响应结果，key为requestId
     */
    private Map<Long, RpcResult> responseResultMap = new ConcurrentHashMap<>();

    /**
     * 保存等待某个请求的RPC调用结果的线程，key为requestId
     */
    private Map<Long, Thread> requestWaitingThread = new ConcurrentHashMap<>();

    /**
     * 已经失效了需要清除掉的requestId（客户端已经等待超时了）
     */
    private Set<Long> cleanRequestIdSet = Collections.synchronizedSet(new HashSet<>());

    private ExecutorService managerPool ;

    public Long nextRequestId() {
        return requestIdGenerator.addAndGet(1);
    }

    public void addRpcResult(Long requestId, RpcResult result) {
        if (cleanRequestIdSet.contains(requestId)) {
            // 忽略当前的结果
            cleanRequestIdSet.remove(requestId);
        }
        // 保存结果
        responseResultMap.put(requestId, result);
        // 唤醒线程
        LockSupport.unpark(this.requestWaitingThread.get(requestId));
    }

    public Object sendRequest(Channel channel, RequestPacket requestPacket) throws RpcCallResponseTimeOutException, DataSerializeException, Exception {
        // 封装requestId
        requestPacket.setRequestId(nextRequestId());
        // 发送请求
        channel.writeAndFlush(Framer.encode(requestPacket));
        // 返回数据
        return get(requestPacket.getRequestId(), requestPacket.getTimeout()).getResultObject();
    }

    private RpcResult get(Long requestId , Long timeOut) throws RpcCallResponseTimeOutException {
        // 保存数等待线程的数据
        requestWaitingThread.put(requestId, Thread.currentThread());
        // 休眠当前的线程，除非被唤醒或者到达了timeout
        LockSupport.parkNanos(timeOut * 1000000);
        // 休眠结束
        if (responseResultMap.get(requestId) == null) {
            // 超时，需要清除当前requestId
            cleanRequestIdSet.add(requestId);
            throw new RpcCallResponseTimeOutException("调用超时");
        }
        // 被结果唤醒，则返回结果
        RpcResult rpcResult = responseResultMap.remove(requestId);
        requestWaitingThread.remove(requestId);
        cleanRequestIdSet.remove(requestId);
        return rpcResult;
    }

}

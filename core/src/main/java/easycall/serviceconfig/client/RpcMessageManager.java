package easycall.serviceconfig.client;

import easycall.codec.packet.RequestPacket;
import easycall.exception.DataSerializeException;
import easycall.exception.RpcCallResponseTimeOutException;
import easycall.network.client.RpcResult;
import io.netty.channel.Channel;

/**
 * RPC消息管理器，负责消息的发送和响应消息的整理
 * @author 翁富鑫 2019/3/28 13:10
 */
public interface RpcMessageManager {

    /**
     * 生成请求id
     */
    Long nextRequestId() ;

    /**
     * 添加Rpc返回结果
     * @param requestId 请求的id
     * @param result 返回的实体类
     */
    void addRpcResult(Long requestId, RpcResult result) ;

    /**
     * 发送RPC请求，并等待RPC响应
     * @param channel 要发送请求的channel
     * @param requestPacket 发送的数据
     * @return
     * @throws RpcCallResponseTimeOutException 响应数据超时
     * @throws DataSerializeException RPC请求数据序列化异常
     * @throws InterruptedException 中断异常
     * @throws Exception
     */
    Object sendRequest(Channel channel, RequestPacket requestPacket) throws RpcCallResponseTimeOutException, DataSerializeException, InterruptedException, Exception ;

}

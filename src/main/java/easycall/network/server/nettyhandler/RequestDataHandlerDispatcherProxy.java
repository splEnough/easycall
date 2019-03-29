package easycall.network.server.handler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.Packet;
import easycall.codec.packet.RequestPacket;
import easycall.exception.*;
import easycall.network.server.RequestHandler.HeartBeatHandler;
import easycall.network.server.RequestHandler.ServiceDataHandler;
import easycall.config.ServerInitializer;
import easycall.serviceconfig.server.RPCProvider;
import easycall.serviceconfig.server.RpcProviderManager;
import easycall.thread.ExecutorManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * 真正的请求数据处理器
 * @author 翁富鑫 2019/3/25 11:22
 */
public class RequestDataHandlerDispatcherProxy {

    /**
     * 写数据的Channel
     */
    private Channel channel;

    /**
     * 请求的数据
     */
    private ByteBuf buf;

    /**
     * 执行管理器
     */
    private ExecutorManager executorManager;

    /**
     * RpcProvider的管理器
     */
    private RpcProviderManager rpcProviderManager;

    private ServerInitializer serverInitializer;

    public RequestDataHandlerDispatcherProxy(Channel channel, ByteBuf buf, ExecutorManager executorManager, RpcProviderManager rpcProviderManager, ServerInitializer serverInitializer) {
        this.channel = channel;
        this.buf = buf;
        this.executorManager = executorManager;
        this.rpcProviderManager = rpcProviderManager;
        this.serverInitializer = serverInitializer;
    }

    public void handle() {
        Packet packet = null;
        try {
            packet = Framer.decode(buf);
            if (packet instanceof RequestPacket) {
                RequestPacket requestPacket = (RequestPacket) packet;
                switch (packet.getMessageType()) {
                    case HEARTBEAT_REQUEST:
                        // 处理心跳
                        executorManager.submitTask(new HeartBeatHandler(channel , requestPacket, null , serverInitializer));
                        break;
                    case SERVICE_DATA_REQUEST:
                        // 处理业务请求
                        handleService(requestPacket);
                        break;
                }
            } else {
                System.out.println("不支持的请求类型");
                throw new UnSupportedException("不支持的请求数据类型：" + packet.getMessageType().name());
            }
        } catch (MagicCheckException e) {
            // 魔数错误不做处理
            e.printStackTrace();
        } catch (UnSupportedException e) {
            e.printStackTrace();
        } catch (DataSerializeException e) {
            e.printStackTrace();
        } catch (DataDeSerializeException e) {
            e.printStackTrace();
        } catch (RpcServiceNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO 返回错误信息
            e.printStackTrace();
        }
    }

    private void handleService(RequestPacket requestPacket) throws RpcServiceNotFoundException {
        String serviceName = requestPacket.getTargetService();
        String version = requestPacket.getTargetVersion();
        RPCProvider rpcProvider = rpcProviderManager.getProviderByServiceName(serviceName , version);
        System.out.println("requestServiceName:" + serviceName + ",version:" + version);
        if (rpcProvider == null) {
            throw new RpcServiceNotFoundException("服务不存在");
        }
        if (rpcProvider.getExecutorService() != null) {
            // 数据处理的任务交由其专属线程执行
            rpcProvider.getExecutorService().submit(new ServiceDataHandler(channel , requestPacket, rpcProvider, serverInitializer));
        } else {
            // 交由公共的处理线程池进行处理
            executorManager.submitTask(new ServiceDataHandler(channel , requestPacket, rpcProvider, serverInitializer));
        }
    }

}

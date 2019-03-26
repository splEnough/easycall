package easycall.network.server.nettyhandler;

import easycall.codec.frame.Framer;
import easycall.codec.packet.MessageType;
import easycall.codec.packet.Packet;
import easycall.codec.packet.RequestPacket;
import easycall.codec.packet.ResponsePacket;
import easycall.codec.serializer.SerializeType;
import easycall.exception.*;
import easycall.network.server.RequestHandler.HeartBeatHandler;
import easycall.thread.ExecutorManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import sun.plugin2.main.server.HeartbeatThread;

import java.util.ArrayList;
import java.util.List;

import static easycall.exception.ResultCode.UN_SUPPORTED_ERROR;

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

    public RequestDataHandlerDispatcherProxy(Channel channel, ByteBuf buf, ExecutorManager executorManager) {
        this.channel = channel;
        this.buf = buf;
        this.executorManager = executorManager;
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
                        executorManager.submitTask(new HeartBeatHandler(channel , requestPacket));
                        break;
                    case SERVICE_DATA_REQUEST:
                        // 处理业务请求
                        handleService(requestPacket);
                        break;
                }
            } else {
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
        } catch (Exception e) {
            // TODO 返回错误信息
        }
    }

    private void handleService(RequestPacket requestPacket) {
        // TODO 判断当前要调用的服务是否有单独的线程
    }

}

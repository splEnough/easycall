package wfx.network.common.frame;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.StringUtil;
import wfx.network.common.packet.MessageType;
import wfx.network.common.packet.Packet;
import wfx.network.common.packet.RequestPacket;
import wfx.network.common.packet.ResponsePacket;
import wfx.network.common.protocol.RpcProtocol;
import wfx.network.common.protocol.RpcProtocolCodec;
import wfx.network.common.serializer.SerializeType;
import wfx.network.common.serializer.SerializerResolver;

import static wfx.network.common.protocol.RpcProtocol.MAGIC;
import static wfx.network.common.protocol.RpcProtocol.ParamTypeAndValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于进行数据包的装包与拆包
 * 装包(decode)：利用传输过来的按照RpcProtocol规定规则排列的字节装包为Packet
 * 拆包(encode)：将Packet数据解析为使用RpcProtocol所对应的字节
 * @author 翁富鑫 2019/3/7 16:46
 */
public class Framer {

    /**
     * 使用RPC协议对Packet数据编码
     * @param packet 要编码的数据
     */
    public static ByteBuf encode(Packet packet) throws Exception {
        RpcProtocol protocol = new RpcProtocol();
        if (packet instanceof RequestPacket) {
            // 封装请求数据包
            RequestPacket requestPacket = (RequestPacket) packet;
            // 数据类型
            protocol.setType((byte)packet.getMessageType().ordinal());
            // 超时时间
            protocol.setTimeout(requestPacket.getTimeout());
            // 序列化类型
            protocol.setSerializeType((byte)packet.getSerializeType().ordinal());
            // 目标业务名
            protocol.setServiceName(requestPacket.getTargetService().getBytes());
            // 目标方法名
            protocol.setMethodName(requestPacket.getTargetMethod().getBytes());
            // 参数个数
            protocol.setMethodParamCount(packet.getObjectTypeNames().size());
            // 请求参数数据
            List<ParamTypeAndValue> paramTypeAndValueList = new ArrayList<>();
            List<Object> requestObjects = requestPacket.getObjects();
            if (requestObjects != null && requestObjects.size() > 0) {
                for (Object o : requestObjects) {
                    // 参数类型名
                    String typeName = o.getClass().getTypeName();
                    int typeNameLength = typeName.length();
                    // 参数数据
                    byte[] serializeData = SerializerResolver.serialize(o, requestPacket.getSerializeType());
                    ParamTypeAndValue paramTypeAndValue = new ParamTypeAndValue(typeNameLength, typeName.getBytes(),serializeData.length, serializeData);
                    paramTypeAndValueList.add(paramTypeAndValue);
                }
            }
            protocol.setParamValues(paramTypeAndValueList);
            return RpcProtocolCodec.encode(protocol);
        } else {
            // TODO 实现服务端
            return RpcProtocolCodec.encode(protocol);
        }
    }

    public static Packet decode(ByteBuf byteBuf) throws Exception{
        // ByteBuf数据解码为RpcProtocol
        RpcProtocol rpcProtocol = RpcProtocolCodec.decode(byteBuf);
        MessageType messageType = MessageType.getTypeByOrdinal(rpcProtocol.getType());
        if (messageType == MessageType.HEARTBEAT_REQUEST || messageType == MessageType.SERVICE_DATA_REQUEST) {
            RequestPacket packet = new RequestPacket();
            packet.setMessageType(messageType);
            packet.setSerializeType(SerializeType.getTypeByOrdinal(rpcProtocol.getSerializeType()));
            List<Object> transObjects = new ArrayList<>();
            List<String> objectTypeNames = new ArrayList<>();
            // 数据反序列化
            for (ParamTypeAndValue  paramTypeAndValue : rpcProtocol.getParamValues()) {
                transObjects.add(SerializerResolver.deSerialize(paramTypeAndValue.getParamValue(), packet.getSerializeType()));
                objectTypeNames.add(new String(paramTypeAndValue.getParamTypeName()));
            }
            packet.setTransObjects(transObjects);
            packet.setParamTypeNames(objectTypeNames);
            // 服务相关信息
            packet.setTargetService(new String(rpcProtocol.getServiceName()));
            packet.setTargetMethod(new String(rpcProtocol.getMethodName()));
            packet.setTimeout(rpcProtocol.getTimeout());
            return packet;
        } else {
            // TODO 实现数据回复
        }
        return null;
    }

}

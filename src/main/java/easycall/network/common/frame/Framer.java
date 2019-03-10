package easycall.network.common.frame;

import io.netty.buffer.ByteBuf;
import easycall.network.common.packet.MessageType;
import easycall.network.common.packet.Packet;
import easycall.network.common.packet.RequestPacket;
import easycall.network.common.packet.ResponsePacket;
import easycall.network.common.protocol.RpcProtocol;
import easycall.network.common.protocol.RpcProtocolCodec;
import easycall.network.common.serializer.SerializeType;
import easycall.network.common.serializer.SerializerResolver;

import static easycall.network.common.protocol.RpcProtocol.ParamTypeAndValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于进行数据包的装包与拆包
 * 装包(decode)：利用传输过来的按照RpcProtocol规定规则排列的字节装包为Packet
 * 拆包(encode)：将Packet数据解析为使用RpcProtocol所对应的字节
 * 写出流程：用户封装Packet -> encode()方法封装为RpcProtocol -> RpcProtocolCodec.encode() 方法转化为ByteBuf
 * 读取流程：接受到ByteBuf -> RpcProtocolCodec.decode()方法解析为RpcProtocol -> decode()方法解析为Packet
 *
 * @author 翁富鑫 2019/3/7 16:46
 */
public class Framer {

    /**
     * 使用RPC协议对Packet数据编码
     *
     * @param packet 要编码的数据
     */
    public static ByteBuf encode(Packet packet) throws Exception {
        RpcProtocol protocol = new RpcProtocol();
        if (packet instanceof RequestPacket) {
            // 封装请求数据包
            RequestPacket requestPacket = (RequestPacket) packet;
            // 数据类型
            protocol.setType((byte) packet.getMessageType().ordinal());
            // 请求id
            protocol.setRequestId(packet.getRequestId());
            // 超时时间
            protocol.setTimeout(requestPacket.getTimeout());
            // 序列化类型
            protocol.setSerializeType((byte) packet.getSerializeType().ordinal());
            // 目标业务名
            protocol.setServiceName(requestPacket.getTargetService().getBytes());
            // 目标方法名
            protocol.setMethodName(requestPacket.getTargetMethod().getBytes());
            // 参数个数
            protocol.setMethodParamCount(packet.getObjectTypeNames().size());
        } else {
            // 响应数据
            ResponsePacket responsePacket = (ResponsePacket) packet;
            // 消息类型
            protocol.setType((byte) responsePacket.getMessageType().ordinal());
            // 对应的请求id
            protocol.setRequestId(responsePacket.getRequestId());
            // 超时disabled
            protocol.setTimeout(-1);
            // 序列化类型
            protocol.setSerializeType((byte) responsePacket.getSerializeType().ordinal());
            // 业务名，响应数据不需要，默认值为null
            protocol.setServiceName("null".getBytes());
            // 方法名，响应数据不需要，默认为null
            protocol.setMethodName("null".getBytes());
            // 参数个数，默认为0
            protocol.setMethodParamCount(0);
        }
        // 请求参数数据
        List<ParamTypeAndValue> paramTypeAndValueList = new ArrayList<>();
        List<Object> requestObjects = packet.getObjects();
        if (requestObjects != null && requestObjects.size() > 0) {
            for (Object o : requestObjects) {
                // 参数类型名
                String typeName = o.getClass().getTypeName();
                int typeNameLength = typeName.length();
                // 参数数据
                byte[] serializeData = SerializerResolver.serialize(o, packet.getSerializeType());
                ParamTypeAndValue paramTypeAndValue = new ParamTypeAndValue(typeNameLength, typeName.getBytes(), serializeData.length, serializeData);
                paramTypeAndValueList.add(paramTypeAndValue);
            }
        }
        protocol.setParamValues(paramTypeAndValueList);
        return RpcProtocolCodec.encode(protocol);
    }

    /**
     * 字节数据解码为Packet，不关心是客户端还是服务端的数据，根据字节码中的数据可能会返回ResponsePacket或RequestPacket
     * 所以需要调用方进行判断
     * @param byteBuf 完整的一次请求数据
     * @throws Exception
     */
    public static Packet decode(ByteBuf byteBuf) throws Exception {
        // ByteBuf数据解码为RpcProtocol
        RpcProtocol rpcProtocol = RpcProtocolCodec.decode(byteBuf);
        MessageType messageType = MessageType.getTypeByOrdinal(rpcProtocol.getType());
        if (messageType == MessageType.HEARTBEAT_REQUEST || messageType == MessageType.SERVICE_DATA_REQUEST) {
            RequestPacket packet = new RequestPacket();
            // 消息类型
            packet.setMessageType(messageType);
            // 请求id
            packet.setRequestId(rpcProtocol.getRequestId());
            // 序列化类型
            packet.setSerializeType(SerializeType.getTypeByOrdinal(rpcProtocol.getSerializeType()));
            // 传输的参数数据
            List<Object> transObjects = new ArrayList<>();
            List<String> objectTypeNames = new ArrayList<>();
            // 数据反序列化
            for (ParamTypeAndValue paramTypeAndValue : rpcProtocol.getParamValues()) {
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
            ResponsePacket packet = new ResponsePacket();
            // 消息类型
            packet.setMessageType(messageType);
            // 请求id
            packet.setRequestId(rpcProtocol.getRequestId());
            // 序列化类型
            packet.setSerializeType(SerializeType.getTypeByOrdinal(rpcProtocol.getSerializeType()));
            // 传输的参数数据
            List<Object> transObjects = new ArrayList<>();
            List<String> objectTypeNames = new ArrayList<>();
            // 数据反序列化
            for (ParamTypeAndValue paramTypeAndValue : rpcProtocol.getParamValues()) {
                transObjects.add(SerializerResolver.deSerialize(paramTypeAndValue.getParamValue(), packet.getSerializeType()));
                objectTypeNames.add(new String(paramTypeAndValue.getParamTypeName()));
            }
            packet.setTransObjects(transObjects);
            packet.setParamTypeNames(objectTypeNames);
            return packet;
        }
    }

}

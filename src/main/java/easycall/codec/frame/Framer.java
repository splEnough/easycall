package easycall.codec.frame;

import easycall.codec.packet.MessageType;
import easycall.codec.protocol.ResponseRpcProtocol;
import easycall.codec.protocol.ResponseRpcProtocolCodec;
import easycall.exception.DataDeSerializeException;
import easycall.exception.DataSerializeException;
import easycall.exception.MagicCheckException;
import io.netty.buffer.ByteBuf;
import easycall.codec.packet.Packet;
import easycall.codec.packet.RequestPacket;
import easycall.codec.packet.ResponsePacket;
import easycall.codec.protocol.RequestRpcProtocol;
import easycall.codec.protocol.RequestRpcProtocolCodec;
import easycall.codec.serializer.SerializeType;
import easycall.codec.serializer.SerializerResolver;
import io.netty.buffer.ByteBufUtil;

import static easycall.codec.protocol.RequestRpcProtocol.ParamTypeAndValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于进行数据包的装包与拆包
 * 装包(decode)：利用传输过来的按照RpcProtocol规定规则排列的字节装包为Packet
 * 拆包(encode)：将Packet数据解析为使用RpcProtocol所对应的字节
 * 写出流程：用户封装Packet -> encode()方法封装为RpcProtocol -> RequestRpcProtocolCodec.encode() 方法转化为ByteBuf
 * 读取流程：接受到ByteBuf -> RequestRpcProtocolCodec.decode()方法解析为RpcProtocol -> decode()方法解析为Packet
 *
 * @author 翁富鑫 2019/3/7 16:46
 */
public class Framer {

    /**
     * 使用RPC协议对Packet数据编码
     *
     * @param packet 要编码的数据
     * @throws DataSerializeException
     * @throws Exception
     */
    public static ByteBuf encode(Packet packet) throws Exception{
        if (packet instanceof RequestPacket) {
            RequestRpcProtocol protocol = new RequestRpcProtocol();
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
            return RequestRpcProtocolCodec.encode(protocol);
        } else {
            ResponseRpcProtocol protocol = new ResponseRpcProtocol();
            // 响应数据
            ResponsePacket responsePacket = (ResponsePacket) packet;
            // 消息类型
            protocol.setType((byte) responsePacket.getMessageType().ordinal());
            // 对应的请求id
            protocol.setRequestId(responsePacket.getRequestId());
            // 序列化类型
            protocol.setSerializeType((byte) responsePacket.getSerializeType().ordinal());
            // 响应码
            protocol.setResultCode(responsePacket.getResultCode());
            // 处理响应对象
            List<Object> responseObjects = packet.getObjects();
            if (responseObjects != null && responseObjects.size() > 0) {
                for (Object o : responseObjects) {
                    // 参数类型名
                    String typeName = o.getClass().getTypeName();
                    int typeNameLength = typeName.length();
                    // 参数数据
                    byte[] serializeData = SerializerResolver.serialize(o, packet.getSerializeType());
                    protocol.setReturnTypeLength(typeNameLength);
                    protocol.setReturnDataTypeName(typeName.getBytes());
                    protocol.setBodyDataLength(serializeData.length);
                    protocol.setBodyData(serializeData);
                }
            }
            return ResponseRpcProtocolCodec.encode(protocol);
        }
    }

    /**
     * 字节数据解码为Packet，不关心是客户端还是服务端的数据，根据字节码中的数据可能会返回ResponsePacket或RequestPacket
     * 所以需要调用方进行判断
     *
     * @param byteBuf 完整的一次请求数据
     * @throws MagicCheckException      没有魔数，抛出异常
     * @throws DataDeSerializeException
     * @throws Exception                反序列化出错
     */
    public static Packet decode(ByteBuf byteBuf) throws Exception {
        // 判断当前为请求或响应
        byte type = byteBuf.getByte(4);
        MessageType messageType = MessageType.getTypeByOrdinal(type);
        if (messageType == MessageType.HEARTBEAT_REQUEST || messageType == MessageType.SERVICE_DATA_REQUEST) {
            // 解码为RpcProtocol
            RequestRpcProtocol rpcProtocol = RequestRpcProtocolCodec.decode(byteBuf);
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
            try {
                // 数据反序列化
                for (ParamTypeAndValue paramTypeAndValue : rpcProtocol.getParamValues()) {
                    Class serializeType = Class.forName(new String(paramTypeAndValue.getParamTypeName()));
                    transObjects.add(SerializerResolver.deSerialize(paramTypeAndValue.getParamValue(), packet.getSerializeType(), serializeType));
                    objectTypeNames.add(new String(paramTypeAndValue.getParamTypeName()));
                }
            } catch (Exception e) {
                throw e;
            }
            packet.setTransObjects(transObjects);
            packet.setTransObjectTypeNames(objectTypeNames);
            // 服务相关信息
            packet.setTargetService(new String(rpcProtocol.getServiceName()));
            packet.setTargetMethod(new String(rpcProtocol.getMethodName()));
            packet.setTimeout(rpcProtocol.getTimeout());
            return packet;
        } else {
            // 解码为RpcProtocol
            ResponseRpcProtocol rpcProtocol = ResponseRpcProtocolCodec.decode(byteBuf);
            ResponsePacket packet = new ResponsePacket();
            // 消息类型
            packet.setMessageType(messageType);
            // 请求id
            packet.setRequestId(rpcProtocol.getRequestId());
            // 序列化类型
            packet.setSerializeType(SerializeType.getTypeByOrdinal(rpcProtocol.getSerializeType()));
            // 返回状态码
            packet.setResultCode(rpcProtocol.getResultCode());
            // 传输的参数数据
            List<Object> transObjects = new ArrayList<>();
            List<String> objectTypeNames = new ArrayList<>();
            // 获取对象类型
            Class serializeType = Class.forName(new String(rpcProtocol.getReturnDataTypeName()));
            // 对象字节反序列化
            transObjects.add(SerializerResolver.deSerialize(rpcProtocol.getBodyData(), packet.getSerializeType(), serializeType));
            objectTypeNames.add(new String(rpcProtocol.getReturnDataTypeName()));
            packet.setTransObjects(transObjects);
            packet.setTransObjectTypeNames(objectTypeNames);
            return packet;
        }
    }

}

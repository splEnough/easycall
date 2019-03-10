package easycall.network.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import static easycall.network.common.protocol.RpcProtocol.ParamTypeAndValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于将RpcProtocol中的数据编码成字节或者从字节中读取数据解码成RpcProtocol
 * @author 翁富鑫 2019/3/9 14:46
 */
public class RpcProtocolCodec {

    /**
     * 将RpcProtocol中的数据编码成ByteBuf
     * @param rpcProtocol 要编码的协议数据
     * @return 编码后的字节数据
     */
    public static ByteBuf encode(RpcProtocol rpcProtocol) {
        ByteBuf byteBuf = Unpooled.buffer();
        // 写入魔数
        byteBuf.writeBytes(rpcProtocol.MAGIC);
        // 写入类型
        byteBuf.writeByte(rpcProtocol.getType());
        // 写入请求id
        byteBuf.writeLong(rpcProtocol.getRequestId());
        // 序列化类型
        byteBuf.writeByte(rpcProtocol.getSerializeType());
        // 超时时间
        byteBuf.writeLong(rpcProtocol.getTimeout());
        // 服务名长度
        byteBuf.writeInt(rpcProtocol.getServiceNameLength());
        // 方法名长度
        byteBuf.writeInt(rpcProtocol.getMethodNameLength());
        // 方法参数个数
        byteBuf.writeInt(rpcProtocol.getMethodParamCount());
        // 服务名
        byteBuf.writeBytes(rpcProtocol.getServiceName());
        // 方法名
        byteBuf.writeBytes(rpcProtocol.getMethodName());
        int bodyLength = 0;
        ByteBuf bodyData = Unpooled.buffer();
        for (RpcProtocol.ParamTypeAndValue paramTypeAndValue : rpcProtocol.getParamValues()) {
            bodyLength += paramTypeAndValue.getProtocolData().length;
            bodyData.writeBytes(paramTypeAndValue.getProtocolData());
        }
        // 写入数据体长度
        byteBuf.writeInt(bodyLength);
        // 写入数据体
        byteBuf.writeBytes(bodyData);
        ReferenceCountUtil.release(bodyData);
        return byteBuf;
    }

    /**
     * 将完整的ByteBuf字节数据解码成为RpcProtocol
     * @param byteBuf 要解码的完整的一次数据
     * @return
     * @throws Exception
     */
    public static RpcProtocol decode(ByteBuf byteBuf) throws Exception{
        byte[] magic = new byte[4];
        byteBuf.readBytes(magic);
        // 魔数校验
        if (!Arrays.equals(RpcProtocol.MAGIC,magic)) {
            throw new Exception("魔数错误，无法处理");
        }
        RpcProtocol rpcProtocol = new RpcProtocol();
        // 消息类型
        byte type = byteBuf.readByte();
        rpcProtocol.setType(type);
        // 请求id
        long requestId = byteBuf.readLong();
        rpcProtocol.setRequestId(requestId);
        // 序列化类型
        byte serializeType = byteBuf.readByte();
        rpcProtocol.setSerializeType(serializeType);
        // 超时时间
        long timeout = byteBuf.readLong();
        rpcProtocol.setTimeout(timeout);
        // 服务名长
        int serviceNameLength = byteBuf.readInt();
        // 方法名长
        int methodNameLength = byteBuf.readInt();
        // 方法参数个数
        int methodParamCount = byteBuf.readInt();
        rpcProtocol.setMethodParamCount(methodParamCount);
        // 服务名
        rpcProtocol.setServiceName(ByteBufUtil.getBytes(byteBuf.readBytes(serviceNameLength)));
        // 方法名
        rpcProtocol.setMethodName(ByteBufUtil.getBytes(byteBuf.readBytes(methodNameLength)));
        // 数据体长度
        int bodyLength = byteBuf.readInt();
        rpcProtocol.setBodyDataLength(bodyLength);
        // 数据体
        ByteBuf bodyData = byteBuf.readBytes(bodyLength);
        List<ParamTypeAndValue> paramTypeAndValueList = decodeParamTypeAndValue(bodyData);
        rpcProtocol.setParamValues(paramTypeAndValueList);
        return rpcProtocol;
    }

    /**
     * 从字节数组中将数据解析为对象
     */
    private static List<ParamTypeAndValue> decodeParamTypeAndValue(ByteBuf bodyData) {
        List<ParamTypeAndValue> list = new ArrayList<>();
        while (bodyData.readableBytes() > 0) {
            // 参数类型名的长度
            int paramTypeNameLength = bodyData.readInt();
            // 数据值长度
            int paramValueLength = bodyData.readInt();
            // 参数类型名
            byte[] paramTypeName = ByteBufUtil.getBytes(bodyData.readBytes(paramTypeNameLength));
            // 对象数据
            byte[] paramValue = ByteBufUtil.getBytes(bodyData.readBytes(paramValueLength));
            ParamTypeAndValue paramTypeAndValue = new ParamTypeAndValue(paramTypeNameLength,paramTypeName,paramValueLength,paramValue);
            list.add(paramTypeAndValue);
        }
        return list;
    }
}

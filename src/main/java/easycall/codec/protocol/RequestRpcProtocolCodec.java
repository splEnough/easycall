package easycall.codec.protocol;

import easycall.exception.MagicCheckException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import static easycall.codec.protocol.RequestRpcProtocol.ParamTypeAndValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于将RequestRpcProtocol中的数据编码成字节或者从字节中读取数据解码成RequestRpcProtocol
 * @author 翁富鑫 2019/3/9 14:46
 */
public class RequestRpcProtocolCodec {

    /**
     * 将RequestRpcProtocol中的数据编码成ByteBuf
     * @param rpcProtocol 要编码的协议数据
     * @return 编码后的字节数据
     */
    public static ByteBuf encode(RequestRpcProtocol rpcProtocol) {
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
        // 版本长度
        byteBuf.writeInt(rpcProtocol.getVersionLength());
        // 方法名长度
        byteBuf.writeInt(rpcProtocol.getMethodNameLength());
        // 方法参数个数
        byteBuf.writeInt(rpcProtocol.getMethodParamCount());
        // 服务名
        byteBuf.writeBytes(rpcProtocol.getServiceName());
        // 版本名
        byteBuf.writeBytes(rpcProtocol.getVersion());
        // 方法名
        byteBuf.writeBytes(rpcProtocol.getMethodName());
        int bodyLength = 0;
        ByteBuf bodyData = Unpooled.buffer();
        for (RequestRpcProtocol.ParamTypeAndValue paramTypeAndValue : rpcProtocol.getParamValues()) {
            bodyLength += paramTypeAndValue.getProtocolData().length;
            bodyData.writeBytes(paramTypeAndValue.getProtocolData());
        }
        // 写入数据体长度
        byteBuf.writeInt(bodyLength);
        // 写入数据体
        byteBuf.writeBytes(bodyData);
        return byteBuf;
    }

    /**
     * 将完整的ByteBuf字节数据解码成为RequestRpcProtocol
     * @param byteBuf 要解码的完整的一次数据
     * @return
     * @throws Exception
     */
    public static RequestRpcProtocol decode(ByteBuf byteBuf) throws MagicCheckException{
        byte[] magic = new byte[4];
        byteBuf.readBytes(magic);
        // 魔数校验
        if (!Arrays.equals(RequestRpcProtocol.MAGIC,magic)) {
            throw new MagicCheckException("魔数校验失败");
        }
        RequestRpcProtocol rpcProtocol = new RequestRpcProtocol();
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
        // 版本名长
        int versionLength = byteBuf.readInt();
        // 方法名长
        int methodNameLength = byteBuf.readInt();
        // 方法参数个数
        int methodParamCount = byteBuf.readInt();
        rpcProtocol.setMethodParamCount(methodParamCount);
        // 服务名
        ByteBuf serviceNameBuf = byteBuf.readBytes(serviceNameLength);
        rpcProtocol.setServiceName(ByteBufUtil.getBytes(serviceNameBuf));
        ReferenceCountUtil.release(serviceNameBuf);
        // 版本名
        ByteBuf versionNameBuf = byteBuf.readBytes(versionLength);
        rpcProtocol.setVersion(ByteBufUtil.getBytes(versionNameBuf));
        ReferenceCountUtil.release(versionNameBuf);
        // 方法名
        ByteBuf methodNameBuf = byteBuf.readBytes(methodNameLength);
        rpcProtocol.setMethodName(ByteBufUtil.getBytes(methodNameBuf));
        ReferenceCountUtil.release(methodNameBuf);
        // 数据体长度
        int bodyLength = byteBuf.readInt();
        rpcProtocol.setBodyDataLength(bodyLength);
        // 数据体
        ByteBuf bodyData = byteBuf.readBytes(bodyLength);
        List<ParamTypeAndValue> paramTypeAndValueList = decodeParamTypeAndValue(bodyData);
        ReferenceCountUtil.release(bodyData);
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
            ByteBuf paramTypeNameBuf = bodyData.readBytes(paramTypeNameLength);
            byte[] paramTypeName = ByteBufUtil.getBytes(paramTypeNameBuf);
            ReferenceCountUtil.release(paramTypeNameBuf);
            // 对象数据
            ByteBuf paramValueBuf = bodyData.readBytes(paramValueLength);
            byte[] paramValue = ByteBufUtil.getBytes(paramValueBuf);
            ReferenceCountUtil.release(paramValueBuf);
            ParamTypeAndValue paramTypeAndValue = new ParamTypeAndValue(paramTypeNameLength,paramTypeName,paramValueLength,paramValue);
            list.add(paramTypeAndValue);
        }
        return list;
    }
}

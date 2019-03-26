package easycall.codec.protocol;

import easycall.exception.MagicCheckException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import java.util.Arrays;

/**
 * 用于将ResponseRpcProtocolCodec中的数据编码成字节或者从字节中读取数据解码成ResponseRpcProtocolCodec
 * @author 翁富鑫 2019/3/25 14:56
 */
public class ResponseRpcProtocolCodec {

    /**
     * 将ResponseRpcProtocol中的数据编码成ByteBuf
     * @param rpcProtocol 要编码的协议数据
     * @return 编码后的字节数据
     */
    public static ByteBuf encode(ResponseRpcProtocol rpcProtocol) {
        ByteBuf byteBuf = Unpooled.buffer();
        // 写入魔数
        byteBuf.writeBytes(rpcProtocol.MAGIC);
        // 写入类型
        byteBuf.writeByte(rpcProtocol.getType());
        // 写入请求id
        byteBuf.writeLong(rpcProtocol.getRequestId());
        // 序列化类型
        byteBuf.writeByte(rpcProtocol.getSerializeType());
        // 返回数据类型长度
        byteBuf.writeLong(rpcProtocol.getReturnTypeLength());
        // 返回对象长度
        byteBuf.writeInt(rpcProtocol.getBodyData().length);
        // 返回数据类型
        byteBuf.writeBytes(rpcProtocol.getReturnDataTypeName());
        // 返回对象
        byteBuf.writeBytes(rpcProtocol.getBodyData());
        return byteBuf;
    }

    /**
     * 将完整的ByteBuf字节数据解码成为RpcProtocol
     * @param byteBuf 要解码的完整的一次数据
     * @return
     * @throws MagicCheckException
     */
    public static ResponseRpcProtocol decode(ByteBuf byteBuf) throws MagicCheckException {
        byte[] magic = new byte[4];
        byteBuf.readBytes(magic);
        // 魔数校验
        if (!Arrays.equals(RequestRpcProtocol.MAGIC,magic)) {
            throw new MagicCheckException("魔数校验失败");
        }
        ResponseRpcProtocol rpcProtocol = new ResponseRpcProtocol();
        // 消息类型
        byte type = byteBuf.readByte();
        rpcProtocol.setType(type);
        // 请求id
        long requestId = byteBuf.readLong();
        rpcProtocol.setRequestId(requestId);
        // 序列化类型
        byte serializeType = byteBuf.readByte();
        rpcProtocol.setSerializeType(serializeType);
        // 返回状态码
        int resultCode = byteBuf.readInt();
        rpcProtocol.setResultCode(resultCode);
        // 返回数据类型长度
        int returnTypeNameLength = byteBuf.readInt();
        rpcProtocol.setReturnTypeLength(returnTypeNameLength);
        // 返回对象数据长度
        int returnBodyLength = byteBuf.readInt();
        rpcProtocol.setBodyDataLength(returnBodyLength);
        // 返回对象类型
        ByteBuf nameBuf = byteBuf.readBytes(returnTypeNameLength);
        rpcProtocol.setReturnDataTypeName(ByteBufUtil.getBytes(nameBuf));
        ReferenceCountUtil.release(nameBuf);
        // 返回对象数据
        ByteBuf buf = byteBuf.readBytes(returnBodyLength);
        byte[] bodyData = ByteBufUtil.getBytes(buf);
        ReferenceCountUtil.release(buf);
        rpcProtocol.setBodyData(bodyData);
        return rpcProtocol;
    }

}

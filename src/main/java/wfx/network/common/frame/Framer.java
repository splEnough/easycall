package wfx.network.common.frame;

import io.netty.buffer.ByteBuf;
import wfx.network.common.packet.Packet;

/**
 * @author 翁富鑫 2019/3/7 16:46
 */
public class Framer {

    /**
     * 使用RPC协议对Packet数据编码
     * @param packet 要编码的数据
     * @param buf 编码之后的数据写入到buf中
     */
    public static void encode(Packet packet, ByteBuf buf) {
        // TODO 实现编码

    }
}

package easycall.codec.packet;

/**
 * @author 翁富鑫 2019/3/7 16:54
 */
public class ResponsePacket extends PacketAdapter{

    /**
     * 返回状态码
     */
    private int resultCode;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}

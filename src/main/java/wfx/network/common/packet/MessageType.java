package wfx.network.common.packet;

/**
 * 请求类型，心跳/业务
 * @author 翁富鑫 2019/3/7 16:27
 */
public enum MessageType {
    HEARTBEAT_REQUEST,
    HEARTBEAT_RESPONSE,
    SERVICE_DATA_REQUEST,
    SERVICE_DATA_RESPONSE,
    TEST;

    public static MessageType getTypeByOrdinal(int ordinal) {
        switch (ordinal) {
            case 0 : return HEARTBEAT_REQUEST;
            case 1 : return HEARTBEAT_RESPONSE;
            case 2 : return SERVICE_DATA_REQUEST;
            case 3 : return SERVICE_DATA_RESPONSE;
        }
        return TEST;
    }
}

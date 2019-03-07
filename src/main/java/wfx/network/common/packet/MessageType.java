package wfx.network.common.packet;

/**
 * 请求类型，心跳/业务
 * @author 翁富鑫 2019/3/7 16:27
 */
public enum MessageType {
    HEARTBEAT_REQUEST,
    HEARTBEAT_RESPONSE,
    SERVICE_DATA_REQUEST,
    SERVICE_DATA_RESPONSE
}

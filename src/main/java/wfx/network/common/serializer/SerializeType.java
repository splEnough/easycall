package wfx.network.common.serializer;

/**
 * 系统默认支持的序列化类型，ordinal与Protocol规定的序列化类型相吻合
 * @author 翁富鑫 2019/3/7 16:30
 */
public enum SerializeType {
    PROTOBUF,
    KRYO,
    JDK;
}

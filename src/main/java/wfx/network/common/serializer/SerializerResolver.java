package wfx.network.common.serializer;

import java.io.Serializable;

/**
 * 序列化工具
 * @author 翁富鑫 2019/3/7 20:10
 */
public class SerializerResolver {

    /**
     * 进行对象的序列化
     * @param transObject 要序列化的对象
     * @param serializeType 选择的序列化的类型
     * @return 序列化之后的字节
     * @throws Exception 出现异常抛出错误
     */
    public static byte[] serialize(Object transObject, SerializeType serializeType) throws Exception{
        if (!(transObject instanceof Serializable)) {
            throw new Exception("对象不能被序列化");
        }
        try {
            switch (serializeType) {
                case KRYO:
                    return KryoSerializer.serializeObject(transObject);
                case PROTOBUF:
                    // TODO 实现protobuf序列化
                    return null;
                default:
                    // TODO 实现JDK序列化
                    return null;
            }
        }  catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Object deSerialize(byte[] data , SerializeType serializeType) throws Exception{
        if (data == null || data.length == 0) {
            throw new Exception("数据不能被反序列化");
        }
        try {
            switch (serializeType) {
                case KRYO:
                    return KryoSerializer.deSerialize(data);
                case PROTOBUF:
                    // TODO 实现protobuf反序列化
                    return null;
                default:
                    // TODO 实现JDK反序列化
                    return null;
            }
        }  catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}


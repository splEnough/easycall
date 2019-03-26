package easycall.codec.serializer;

import easycall.exception.DataDeSerializeException;
import easycall.exception.DataSerializeException;

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
     * @throws DataSerializeException
     * @throws Exception 出现异常抛出错误
     */
    public static byte[] serialize(Object transObject, SerializeType serializeType) throws Exception{
        if (!(transObject instanceof Serializable)) {
            throw new DataSerializeException("对象不能被序列化");
        }
        try {
            switch (serializeType) {
                case KRYO:
                    return KryoSerializer.serializeObject(transObject);
                case PROTO_STUFF:
                    return ProtoStuffSerializer.serialize(transObject);
                default:
                    return JdkSerializer.serializeObject(transObject);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 数据反序列化
     * @param data
     * @param serializeType
     * @param dataType
     * @return
     * @throws DataSerializeException
     * @throws Exception
     */
    public static Object deSerialize(byte[] data , SerializeType serializeType, Class dataType) throws Exception{
        if (data == null || data.length == 0) {
            throw new DataDeSerializeException("数据不能被反序列化，数据为空");
        }
        try {
            switch (serializeType) {
                case KRYO:
                    return KryoSerializer.deSerialize(data);
                case PROTO_STUFF:
                    return ProtoStuffSerializer.deSerialize(data, dataType);
                default:
                    return JdkSerializer.deSerialize(data);
            }
        }  catch (Exception e) {
            throw e;
        }
    }
}


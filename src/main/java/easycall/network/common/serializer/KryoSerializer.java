package easycall.network.common.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * kryo序列化器
 * @author 翁富鑫 2019/2/27 16:20
 */
public class KryoSerializer {

    /**
     * 序列化对象
     * @param object 要序列化的对象
     * @return ByteBuf 封装的数据
     */
    public static <T> byte[] serializeObject(T object) throws Exception{
        if (object == null) {
            throw new Exception("序列化的数据不能为空");
        }
        Kryo kryo = new Kryo();
        Output output = new Output(1024);
        byte[] data = null;
        try {
            kryo.writeClassAndObject(output, object);
            data = output.toBytes();
        } catch (Exception e) {
            throw new KryoException("序列化数据失败");
        } finally {
            output.close();
        }
        return data;
    }

    /**
     * 反序列化
     * @param data 要反序列化的字节
     */
    public static Object deSerialize(byte[] data) throws Exception{
        if (data == null || data.length == 0) {
            throw new Exception("反序列化的数据不能为空");
        }
        Kryo kryo = new Kryo();
        Input input = new Input(data);
        Object result;
        try {
            result = kryo.readClassAndObject(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            input.close();
        }
        return result;
    }

}

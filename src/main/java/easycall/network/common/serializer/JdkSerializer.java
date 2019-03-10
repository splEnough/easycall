package easycall.network.common.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;

/**
 * jdk序列化
 * @author 翁富鑫 2019/3/10 17:58
 */
public class JdkSerializer {
    /**
     * 序列化对象
     * @param object 要序列化的对象
     * @return ByteBuf 封装的数据
     */
    public static <T> byte[] serializeObject(T object) throws Exception{
        if (object == null) {
            throw new Exception("序列化的数据不能为空");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        try {
            objectOutputStream.writeObject(object);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            outputStream.close();
            objectOutputStream.close();
        }
    }

    /**
     * 反序列化
     * @throws Exception
     */
    public static Object deSerialize(byte[] data) throws Exception{
        if (data == null || data.length == 0) {
            throw new Exception("反序列化的数据不能为空");
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        try {
            return objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            byteArrayInputStream.close();
            objectInputStream.close();
        }
    }
}

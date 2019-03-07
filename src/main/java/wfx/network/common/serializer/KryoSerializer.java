package wfx.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author 翁富鑫 2019/2/27 16:20
 */
public class KryoSerializer {

    /**
     * 序列化对象
     * @param object 要序列化的对象
     * @return ByteBuf 封装的数据
     */
    public static <T> byte[] serializeObject(T object) throws Exception{
        long begin = System.currentTimeMillis();
        Kryo kryo = new Kryo();
        Output output = new Output(1024);
        byte[] data = null;
        try {
            kryo.writeClassAndObject(output, object);
            data = output.toBytes();
            long end = System.currentTimeMillis();
            System.out.println("serialize cost:" + (end - begin));
        } catch (Exception e) {
            throw new KryoException("序列化数据失败");
        } finally {
            output.close();
        }
        return data;
    }

    public static Object deSerialize(byte[] data) throws Exception{
        long begin = System.currentTimeMillis();
        Kryo kryo = new Kryo();
        Input input = new Input(data);
        Object result;
        try {
            result = kryo.readClassAndObject(input);
            long end = System.currentTimeMillis();
            System.out.println("deSerialize cost:" + (end - begin));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            input.close();
        }
        return result;
    }

}

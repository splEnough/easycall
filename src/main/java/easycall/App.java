package easycall;

import easycall.Util.StringUtil;
import easycall.codec.serializer.JdkSerializer;
import easycall.codec.serializer.ProtoStuffSerializer;
import easycall.test.vo.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception{
//        System.out.println(('E' - 'e'));
//        String msg = "e21";
//        System.out.println(StringUtil.firstCharToLow(msg));
//        System.out.println(StringUtil.equals("" , ""));
//        System.out.println(StringUtil.equals("" , null));
//        System.out.println(StringUtil.equals(null , null));
//        System.out.println(StringUtil.equals("123" , "123"));

        testSerialize();


    }

    private static void readFile() throws Exception{
        InputStream inputStream = new FileInputStream("D:\\resources\\test");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = inputStream.read(data)) != -1) {
            byteArrayOutputStream.write(data, 0 , len);
        }
        byte[] result = byteArrayOutputStream.toByteArray();
        System.out.println("readLength:" + result.length);
        List<Person> personList = (List<Person>)JdkSerializer.deSerialize(result);
        System.out.println(personList);
    }

    private static void testSerialize() throws Exception{
        List<Person> personList = new ArrayList<>();
        for (int i = 0;i < 3;i ++) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("key" , "value:" + i);
            paramMap.put("key" , "value:" + i + 1);
            Person person = new Person();
            person.setAge(i);
            person.setName("ads" + i);
            person.setParams(paramMap);
            personList.add(person);
        }
        byte[] data = ProtoStuffSerializer.serialize(personList);
        System.out.println("data.length:" + data.length);

        ArrayList arrayList = ProtoStuffSerializer.deserialize(java.util.ArrayList.class, data);
        System.out.println(arrayList);
    }
}


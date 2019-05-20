package easycall.test;

import com.esotericsoftware.kryo.Kryo;
import easycall.codec.packet.Packet;
import easycall.codec.serializer.JdkSerializer;
import easycall.codec.serializer.KryoSerializer;
import easycall.codec.serializer.ProtoStuffSerializer;
import easycall.test.vo.SerializeVo;

import java.io.Serializable;
import java.util.*;

/**
 * @author 翁富鑫 2019/5/19 13:31
 */
public class SerializeVoGenerator {
    public static void main(String[] args) throws Exception {
        System.out.println("参数初始化完毕");
        Map<String,Map<Integer,Long>> serializeCost = new HashMap<>();
        serializeCost.put("jdk" , new HashMap<>());
        serializeCost.put("kryo" , new HashMap<>());
        serializeCost.put("protostuff" , new HashMap<>());
        Map<String,List<Integer>> serializeSize = new HashMap<>();
        serializeSize.put("jdk" , new ArrayList<>());
        serializeSize.put("kryo" , new ArrayList<>());
        serializeSize.put("protostuff" , new ArrayList<>());
        for (int i = 0;i < 10;i++) {
            List<SerializeVo> vos = new ArrayList<>();
            PackageVo vo = new PackageVo(vos);
            for (int j = 0;j < 500000;j++) {
                SerializeVo serializeVo = new SerializeVo();
                serializeVo.setAge(i + j );
                serializeVo.setDate(new Date());
                serializeVo.setSalary(213.234 + i + j );
                serializeVo.setName("nameadada" + i  + "," + j);
                List<String> jobs = new ArrayList<>();
                for (int k = 0; k < 3;k++) {
                    jobs.add("jobjobjobojoaodaodusadad" + i + j + k);
                }
                serializeVo.setJobs(jobs);
                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("address" , "hangzhou"+ i + "," + j);
                paramMap.put("name" , "safdsad" + i + "," + j);
                paramMap.put("age" , "sards" + i + "," + j);
                serializeVo.setMap(paramMap);
                Set<String> sets = new HashSet<>();
                sets.add("asd" +i  + "," + j);
                sets.add("asd23" +i  + "," + j);
                sets.add("asd23453" +i + "," + j);
                serializeVo.setSet(sets);
                vos.add(serializeVo);
            }
            // jdk
            long jdkBegin = System.currentTimeMillis();
            int jdkSize = JdkSerializer.serializeObject(vo).length;
            serializeSize.get("jdk").add(jdkSize);
            long jekEnd = System.currentTimeMillis();
            long jdkCost = jekEnd - jdkBegin;
            serializeCost.get("jdk").put(i, jdkCost);


            long kryoBegin = System.currentTimeMillis();
            int kryoSize = KryoSerializer.serializeObject(vo).length;
            serializeSize.get("kryo").add(kryoSize);
            long kryoEnd = System.currentTimeMillis();
            long kryoCost = kryoEnd - kryoBegin;
            serializeCost.get("kryo").put(i, kryoCost);

            long protostuffBegin = System.currentTimeMillis();
            int proSize = ProtoStuffSerializer.serialize(vo).length;
            serializeSize.get("protostuff").add(proSize);
            long proEnd = System.currentTimeMillis();
            long proCost = proEnd - protostuffBegin;
            serializeCost.get("protostuff").put(i, proCost);
        }

        serializeCost.forEach((key, map) -> {
            System.out.println(key);
            map.forEach((time , value) -> {
                System.out.println("index: " + time + ",value:" + value);
            });
        });

        serializeSize.forEach((key , list) -> {
            System.out.println(key);
            for (int i =0 ; i < list.size();i++) {
                System.out.println("index:" + i + ",size:" + list.get(i));
            }
        });
    }
}

class PackageVo implements Serializable {
    private List<SerializeVo> serializeVos;

    public PackageVo(List<SerializeVo> serializeVos) {
        this.serializeVos = serializeVos;
    }
}


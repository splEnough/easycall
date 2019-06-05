package wfx.service;

import wfx.provider.Provider;
import wfx.vo.Member;

import java.time.LocalDate;
import java.util.*;

/**
 * 测试服务提供方
 * @author fuxin.wfx 2019/6/4 19:12
 */
@Provider(interfaceClass = MultiTestService.class)
public class MultiTestServiceImpl implements MultiTestService {
    @Override
    public Member buildMember(String name, Integer age, Double salary, LocalDate birthday) {
        System.out.println("property:" + System.getProperty("serverId"));
        Member person = new Member(name, age, birthday, salary);
        System.out.println(person);
        return person;
    }

    @Override
    public List<Member> getMemberList(ArrayList<Integer> ages) {
        List<Member> persons = new ArrayList<>();
        for (Integer age : ages) {
            Member person = new Member("name-" + age, age, LocalDate.now(), 123.0+age);
            persons.add(person);
        }
        return persons;
    }

    @Override
    public Map<String, Member> getMemberMap(ArrayList<String> keys) {
        Map<String,Member> personMap = new HashMap<>();
        Random random = new Random();
        for (String key : keys) {
            Member member = new Member(key , random.nextInt(20), LocalDate.now(), random.nextDouble());
            personMap.put(key, member);
        }
        return personMap;
    }

    @Override
    public void printMemberMsg(Member member) {
        System.out.println(member);
    }
}

package easycall.test.impl;

import easycall.test.PersonService;
import easycall.test.vo.Person;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 翁富鑫 2019/3/29 10:30
 */
public class PersonServiceImpl implements PersonService {
    @Override
    public Person getPerson(String name, Integer age) {
        System.out.println("PersonServiceImpl -- getPerson():name:" + name + ",age:" + age);
        Person person = new Person() ;
        person.setName(name);
        person.setAge(age);
        Map<String,String> params = new HashMap<>();
        params.put("test" , "test");
        person.setParams(params);
        return person;
    }

    @Override
    public Person print(String msg, String param) {
        System.out.println("PersonServiceImpl -- print():msg:" + msg + ",param:" + param);
        return null;
    }

    @Override
    public void doSomething(String msg, String param) {
        System.out.println("PersonServiceImpl -- doSomething():msg:" + msg + ",param:" + param);
    }
}

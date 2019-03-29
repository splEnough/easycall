package wfx.service.impl;

import wfx.service.PersonService;
import wfx.vo.Person;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 翁富鑫 2019/3/29 10:30
 */
public class PersonServiceImpl implements PersonService{
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
}

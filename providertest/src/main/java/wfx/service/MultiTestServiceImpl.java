package wfx.service;

import wfx.provider.Provider;
import wfx.vo.Person;

import java.time.LocalDate;
import java.util.*;

/**
 * @author fuxin.wfx 2019/6/4 19:12
 */
@Provider(interfaceClass = MultiTestService.class)
public class MultiTestServiceImpl implements MultiTestService {
    @Override
    public Person buildPerson(String name, Integer age, Double salary, LocalDate birthday) {
        System.out.println("property:" + System.getProperty("serverId"));
        Person person = new Person(name, age, birthday, salary);
        System.out.println(person);
        return person;
    }

    @Override
    public List<Person> getPersonList(List<Integer> ages) {
        List<Person> persons = new ArrayList<>();
        for (Integer age : ages) {
            Person person = new Person("name-" + age, age, LocalDate.now(), 123.0+age);
            persons.add(person);
        }
        return persons;
    }

    @Override
    public Map<String, Person> getPersonMap(List<String> keys) {
        Map<String,Person> personMap = new HashMap<>();
        Random random = new Random();
        for (String key : keys) {
            Person person = new Person(key , random.nextInt(20), LocalDate.now(), random.nextDouble());
            personMap.put(key, person);
        }
        return personMap;
    }

    @Override
    public void printPersonMsg(Person perosn) {
        System.out.println(perosn);
    }
}

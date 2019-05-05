package easycall.test.impl;

import easycall.test.PersonService;
import easycall.test.vo.Car;
import easycall.test.vo.City;
import easycall.test.vo.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        params.put("haha" , "haha");
        person.setParams(params);
        List<String> jobs = new ArrayList<>();
        jobs.add("engineering");
        jobs.add("manager");
        jobs.add("boss");
        person.setJobs(jobs);

        // 测试包含其他的Bean
        City city = new City();
        city.setAddress("浙江");
        city.setName("杭州");
        person.setCity(city);

        // 测试List<Bean>
        List<Car> cars = new ArrayList<>();
        for (int i = 0;i < 3;i ++) {
            Car car = new Car();
            car.setPrice(1023230.0 + i);
            car.setType(i + "");
            cars.add(car);
        }
        person.setCars(cars);
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

    @Override
    public List<Person> getPersonList(String name) {
        List<Person> personList = new ArrayList<>();
        for (int i = 0;i < 3;i ++) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("key" , "value:" + i);
            paramMap.put("key" , "value:" + i + 1);
            Person person = new Person();
            person.setAge(i);
            person.setName(name + i);
            person.setParams(paramMap);
            personList.add(person);
        }
        System.out.println("返回数据size：" + personList.size());
        return personList;
    }
}

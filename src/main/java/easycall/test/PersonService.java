package easycall.test;

import easycall.test.vo.Person;

/**
 * @author 翁富鑫 2019/3/29 10:28
 */
public interface PersonService {

    Person getPerson(String name, Integer age) ;

    Person print(String msg, String param);

    void doSomething(String msg, String param);

}

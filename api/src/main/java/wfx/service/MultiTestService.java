package wfx.service;

import wfx.vo.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author fuxin.wfx 2019/6/4 18:25
 */
public interface MultiTestService {

    /**
     * 通过参数构建一个Person对象
     * @param name 姓名
     * @param age 年龄
     * @param salary 工资
     * @param birthday 生日
     */
    Person buildPerson(String name, Integer age, Double salary, LocalDate birthday);

    /**
     * 随机构造Person对象列表List
     * @param ages 年龄列表
     */
    List<Person> getPersonList(List<Integer> ages);

    /**
     * 通过key列表随机生成Person对象Map
     * @param keys 对应person中的age
     */
    Map<String,Person> getPersonMap(List<String> keys);

    /**
     * 返回空值
     * @param perosn
     */
    void printPersonMsg(Person perosn);

}

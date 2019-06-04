package wfx.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author fuxin.wfx 2019/6/4 18:26
 */
public class Person implements Serializable {
    private String name;
    private Integer age;
    private LocalDate birthday;
    private Double salary;

    public Person() {}


    public Person(String name, Integer age, LocalDate birthday, Double salary) {
        this.name = name;
        this.age = age;
        this.birthday = birthday;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", birthday=" + formatter.format(birthday) +
                ", salary=" + salary +
                '}';
    }
}

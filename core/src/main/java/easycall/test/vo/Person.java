package easycall.test.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 翁富鑫 2019/2/20 20:08
 */
public class Person implements Serializable {
    private String name ;
    private Integer age;
    private Map<String,String> params;
    private List<String> jobs ;
    private City city;
    private List<Car> cars;

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
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

    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
    }

    @Override
    public String toString() {
        return "name:" + this.name + ",age:" + this.age + ",params:" + params + ",jobs:" + jobs + ",city:" + city
                + ",cars:" + cars;
    }
}

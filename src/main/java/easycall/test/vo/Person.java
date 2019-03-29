package easycall.test.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 翁富鑫 2019/2/20 20:08
 */
public class Person implements Serializable {
    private String name ;
    private Integer age;
    private Map<String,String> params;

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

    @Override
    public String toString() {
        return "name:" + this.name + ",age:" + this.age + ",params:" + params.entrySet().toString();
    }
}

package easycall.test.vo;

import java.io.Serializable;

/**
 * @author 翁富鑫 2019/3/30 16:06
 */
public class City implements Serializable{
    private String name ;
    private String address ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "city,name:" + name + ",address:" + address;
    }
}

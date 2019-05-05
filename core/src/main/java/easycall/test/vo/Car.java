package easycall.test.vo;

import java.io.Serializable;

/**
 * @author 翁富鑫 2019/3/30 16:16
 */
public class Car implements Serializable {
    private String type;
    private Double price;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "type:" + type + ",price:" + price;
    }
}

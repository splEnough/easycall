package easycall.loadbalance;

import easycall.exception.UnknownConfigException;
import easycall.registercenter.client.Subscriber;

/**
 * 负载均衡类型
 * @author 翁富鑫 2019/3/28 16:04
 */
public enum LoadBalanceType {
    RANDOM(0, "随机");

    private int code;
    private String text;

    private LoadBalanceType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static LoadBalanceType getTypeByCode(int code) {
        switch (code) {
            case 0:
                return RANDOM;
            default:
                throw new UnknownConfigException("负载均衡配置错误");
        }
    }

    public static LoadBalancer getLoadBalancerByCode(int code, Subscriber subscriber) {
        switch (code) {
            case 0:
                return new RandomLoadBalancer(subscriber);
            default:
                throw new UnknownConfigException("负载均衡配置错误");
        }
    }
}

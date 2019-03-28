package easycall.loadbalance;

import easycall.registercenter.RegisterCenterClient;

import java.util.List;
import java.util.Random;

/**
 * 随机选取一个ip
 * @author 翁富鑫 2019/3/28 14:45
 */
public class RandomLoadBalancer extends LoadBalancerAdapter {

    public RandomLoadBalancer(RegisterCenterClient registerCenterClient) {
        super(registerCenterClient);
    }

    @Override
    String next0(String service, String version, List<String> ipList) {
        Random random = new Random();
        int size = ipList.size();
        int randomIndex = random.nextInt(size);
        return ipList.get(randomIndex);
    }
}

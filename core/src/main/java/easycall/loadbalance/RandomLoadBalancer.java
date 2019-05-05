package easycall.loadbalance;

import easycall.registercenter.client.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 随机选取一个ip
 * @author 翁富鑫 2019/3/28 14:45
 */
public class RandomLoadBalancer extends LoadBalancerAdapter {

    public RandomLoadBalancer(Subscriber subscriber) {
        super(subscriber);
    }

    @Override
    String next0(String service, String version, Set<String> ipSet) {
        Random random = new Random();
        int size = ipSet.size();
        int randomIndex = random.nextInt(size);
        List<String> ipList = new ArrayList<>(ipSet);
        return ipList.get(randomIndex);
    }
}

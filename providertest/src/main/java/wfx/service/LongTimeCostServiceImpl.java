package wfx.service;

import wfx.provider.Provider;

import java.util.concurrent.TimeUnit;

/**
 * @author 翁富鑫 2019/5/19 14:41
 */
@Provider(interfaceClass = LongTimeCostService.class)
public class LongTimeCostServiceImpl implements LongTimeCostService {
    @Override
    public String handle(String msg) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }
}

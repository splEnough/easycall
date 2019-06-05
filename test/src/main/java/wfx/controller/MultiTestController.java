package wfx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wfx.service.LongTimeCostService;
import wfx.service.MultiTestService;
import wfx.vo.Member;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 远程过程调用演示
 * @author fuxin.wfx 2019/6/4 19:27
 */
@Controller
@RequestMapping("/multiTest/easy")
@ResponseBody
public class MultiTestController {

    @Autowired
    private MultiTestService multiTestService;

    @Autowired
    private LongTimeCostService longTimeCostService;


    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @RequestMapping("/dump")
    public Object testDumpThread() {
        for (int i = 0;i < 6;i++) {
            new Thread(() -> {
                this.longTimeCostService.handle("echo");
            }).start();
        }
        return true;
    }

    @RequestMapping("/detail")
    public Object detail() throws Exception{
        String name = "翁富鑫";
        Integer age = 22;
        Double salary = 100.0;
        LocalDate birthday = LocalDate.of(1997,4,3);
        // 测试Object对象参数
        Member member = multiTestService.buildMember(name, age, salary, birthday);
        System.out.println("Object类型参数：");
        System.out.println(member);
        System.out.println("--------------------------------------------------------------------------");
        // 休眠一秒
        TimeUnit.SECONDS.sleep(2);
        // 测试List容器对象
        ArrayList<Integer> ages = new ArrayList<>();
        Random random = new Random();
        for (int i = 0;i < 3;i++) {
            ages.add(random.nextInt(100));
        }
        System.out.println("List类型参数：");
        List<Member> persons = multiTestService.getMemberList(ages);
        System.out.println(persons);
        System.out.println("--------------------------------------------------------------------------");
        // 休眠一秒
        TimeUnit.SECONDS.sleep(2);
        // 测试Map参数
        ArrayList<String> keys = new ArrayList<>();
        keys.add("key-1");
        keys.add("key-2");
        keys.add("key-3");
        System.out.println("Map类型参数：");
        Map<String,Member> resultMap = this.multiTestService.getMemberMap(keys);
        System.out.println(resultMap);
        System.out.println("--------------------------------------------------------------------------");
        // 无返回数据
        this.multiTestService.printMemberMsg(member);
        System.out.println("处理结束");
        return true;
    }

    @RequestMapping("/put")
    public Object putPerson(String name, Integer age, Double salary, String birthday) {
        long begin = System.currentTimeMillis();
        Member person = multiTestService.buildMember(name, age, salary, LocalDate.parse(birthday, formatter));
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - begin));
        return person;
    }

    @RequestMapping("/getPersonList")
    public Object getPersonList(String ages) {
        String[] ageAray = ages.split(",");
        ArrayList<Integer> ageList = new ArrayList<>();
        for (String s : ageAray) {
            ageList.add(Integer.parseInt(s));
        }
        long begin = System.currentTimeMillis();
        List<Member> persons = multiTestService.getMemberList(ageList);
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - begin));
        return persons;
    }

    @RequestMapping("/getPersonMap")
    public Object getPersonMap(String keys) {
        String[] keyArray = keys.split(",");
        ArrayList<String> keysString = new ArrayList<>(Arrays.asList(keyArray));
        long begin = System.currentTimeMillis();
        Map<String,Member> resultMap = this.multiTestService.getMemberMap(keysString);
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - begin));
        return  resultMap;
    }

    @RequestMapping("/printMsg")
    public Object printMsg(String name, Integer age, Double salary, String birthday) {
        Member person = new Member(name, age, LocalDate.parse(birthday, formatter) , salary);
        try {
            long begin = System.currentTimeMillis();
            this.multiTestService.printMemberMsg(person);
            long end = System.currentTimeMillis();
            System.out.println("cost:" + (end - begin));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}

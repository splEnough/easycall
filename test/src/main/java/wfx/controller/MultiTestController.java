package wfx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wfx.service.MultiTestService;
import wfx.vo.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fuxin.wfx 2019/6/4 19:27
 */
@Controller
@RequestMapping("/multiTest/easy")
@ResponseBody
public class MultiTestController {

    @Autowired
    private MultiTestService multiTestService;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @RequestMapping("/put")
    public Object putPerson(String name, Integer age, Double salary, String birthday) {
        long begin = System.currentTimeMillis();
        Person person = multiTestService.buildPerson(name, age, salary, LocalDate.parse(birthday, formatter));
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - begin));
        return person;
    }

    @RequestMapping("/getPersonList")
    public Object getPersonList(String ages) {
        String[] ageAray = ages.split(",");
        List<Integer> ageList = new ArrayList<>();
        for (String s : ageAray) {
            ageList.add(Integer.parseInt(s));
        }
        long begin = System.currentTimeMillis();
        List<Person> persons = multiTestService.getPersonList(ageList);
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - begin));
        return persons;
    }

    @RequestMapping("/getPersonMap")
    public Object getPersonMap(String keys) {
        String[] keyArray = keys.split(",");
        List<String> keysString = new ArrayList<>(Arrays.asList(keyArray));
        long begin = System.currentTimeMillis();
        Map<String,Person> resultMap = this.multiTestService.getPersonMap(keysString);
        long end = System.currentTimeMillis();
        System.out.println("cost:" + (end - begin));
        return  resultMap;
    }

    @RequestMapping("/printMsg")
    public Object printMsg(String name, Integer age, Double salary, String birthday) {
        Person person = new Person(name, age, LocalDate.parse(birthday, formatter) , salary);
        try {
            long begin = System.currentTimeMillis();
            this.multiTestService.printPersonMsg(person);
            long end = System.currentTimeMillis();
            System.out.println("cost:" + (end - begin));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}

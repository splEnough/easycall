package wfx.cloudcontrolle;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wfx.vo.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author fuxin.wfx 2019/6/4 20:29
 */

@RestController
@RequestMapping("/multiTest/cloud")
public class MultiTestController {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @RequestMapping("/put")
    public Object put(String name, Integer age, Double salary, String birthday) {
        Person person = new Person(name, age, LocalDate.parse(birthday, formatter), salary);
        System.out.println(person);
        return person;
    }

}

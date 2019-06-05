package wfx.service;

import wfx.vo.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fuxin.wfx 2019/6/4 18:25
 */
public interface MultiTestService {

    /**
     * 通过参数构建一个Membern对象
     * @param name 姓名
     * @param age 年龄
     * @param salary 工资
     * @param birthday 生日
     */
    Member buildMember(String name, Integer age, Double salary, LocalDate birthday);

    /**
     * 随机构造Member对象列表List
     * @param ages 年龄列表
     */
    List<Member> getMemberList(ArrayList<Integer> ages);

    /**
     * 通过key列表随机生成Member对象Map
     * @param keys 对应Member中的age
     */
    Map<String,Member> getMemberMap(ArrayList<String> keys);

    /**
     * 返回空值
     * @param member
     */
    void printMemberMsg(Member member);

}

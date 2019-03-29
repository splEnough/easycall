package easycall;

import easycall.Util.StringUtil;

public class App {
    public static void main(String[] args) {
        System.out.println(('E' - 'e'));
        String msg = "e21";
        System.out.println(StringUtil.firstCharToLow(msg));
        System.out.println(StringUtil.equals("" , ""));
        System.out.println(StringUtil.equals("" , null));
        System.out.println(StringUtil.equals(null , null));
        System.out.println(StringUtil.equals("123" , "123"));
    }
}


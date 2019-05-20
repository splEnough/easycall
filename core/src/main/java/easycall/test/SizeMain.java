package easycall.test;

/**
 * @author 翁富鑫 2019/5/19 17:05
 */
public class SizeMain {
    public static void main(String[] args) {
        String s = "174889463,174889463,174889463,174889463,174889463,174889463,174889463,174889463,174889463,174889463";
        String[] array = s.split(",");
        for (String x : array) {
            int value = Integer.parseInt(x);
            System.out.println(value/1024.0/1024.0 + "MB");
        }

        System.out.println(128380795/1024.0/1024.0);
        System.out.println(7900/2100.0);
    }
}

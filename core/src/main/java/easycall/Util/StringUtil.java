package easycall.Util;

/**
 * @author 翁富鑫 2019/3/24 16:06
 */
public class StringUtil {

    /**
     * 首字母小写
     * @param str
     * @return
     */
    public static String firstCharToLow(String str) {
        if ("".equals(str) || str == null) {
            return str;
        }
        char[] chars = str.toCharArray();
        char first = chars[0] ;
        if (first >= 'A' && first <= 'Z') {
            chars[0] = (char) (first + 32);
        }
        return new String(chars);
    }

    public static boolean equals(String s1, String s2) {
        if (s1 != null) {
            return s1.equals(s2);
        } else if (s2 != null) {
            return s2.equals(s1);
        }
        // 都为null
        return true;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}

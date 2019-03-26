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
}

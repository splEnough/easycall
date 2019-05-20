package wfx.test;

import easycall.thread.JvmUtil;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author 翁富鑫 2019/5/19 14:52
 */
public class TestMain {
    public static void main(String[] args) throws Exception{
        OutputStream outputStream = new FileOutputStream("D:\\resources\\learn\\test");
        JvmUtil.jstack(outputStream);
    }
}

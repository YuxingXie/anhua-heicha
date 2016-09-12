package com.lingyun.common.util;

/**
 * Created by Administrator on 2016/9/8.
 */
public class SomeTest {
    public static long begin;
    public static long end;
    public static void printHowLong(){
        System.out.println("begin:"+begin+",end:"+end+",相隔毫秒数："+(end-begin));
    }
}

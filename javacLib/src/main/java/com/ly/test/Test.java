package com.ly.test;

import java.util.regex.Pattern;

/**
 * Created by liuyao-s on 2018/5/4.
 */

public class Test {
    public static void main(String[] args){
        String str = "{hello}";
        String reg = "\\{[a-z]+\\}";
        boolean ret = Pattern.compile(reg).matcher(str).matches();
        System.out.println("ret="+ret);
    }
}

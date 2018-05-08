package com.ly.test;

import com.ly.sa.Input;
import com.ly.sa.Input.*;

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

        Input i = new Input();
        Person pp = i.new Person("ee");
        System.out.println("name = "+pp.getName());
    }
}

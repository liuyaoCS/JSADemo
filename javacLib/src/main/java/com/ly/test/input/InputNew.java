package com.ly.test.input;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyao-s on 2018/4/23.
 */

public class InputNew {
    int a = 1;
    @Deprecated
    public List test_add_head(String s,int a){
        System.out.println("before  code");
        s+=":endl";
        a=0;
        int b =a;

        List<String> lists=new ArrayList<>();
        lists.add(s+a);
        return lists;
    }
    public int test_add_tail(int a){
        a+=2;
        a*=2;

        System.out.println("after  code");
        int b=111;
//        a=b;
//        System.out.println("a="+a);
        return b;
    }
    private int test_add_both(int a){
        System.out.println("1");
        a+=2;
        System.out.println("2");
        return a;
    }
    public String test_replace(){
        String s1 = "hi";
        System.out.println("s="+s1+"end");
        return s1;
    }
    public int test_other(){
        return 2;
    }
}


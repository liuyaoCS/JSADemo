package com.ly.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyao-s on 2018/4/23.
 */

public class Input {
    int a = 1;
    @Deprecated
    public List test_add_head(String s){
        List<String> lists=new ArrayList<>();
        lists.add(s);
        return lists;
    }
    public int test_add_tail(){
        a+=2;
        a*=2;
        return a;
    }
    public String test_replace(){
        String s="hi";
        System.out.println("s="+s);
        return s;
    }

}


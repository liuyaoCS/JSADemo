package com.ly.sa;

import com.ly.test.Single;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Created by liuyao-s on 2018/4/23.
 */

public class Input {
    int a = 1;
    public class Person{
        String name;
        public Person(String s){
            name = s;
        }
        public String getName(){
            return name;
        }
        public void setName(String n){
            name = n;
        }
    }
    @Deprecated
    public List test_add_head(String s,int a){
        List<String> lists=new ArrayList<>();
        lists.add(s+a);
        return lists;
    }
    private int test_add_tail(int a){
        a+=2;
        a*=2;
        return a;
    }
    public String test_replace(HashSet set,int b) throws Exception {
        String s=InputNew.class.getName();
        List a=null;
        System.out.println("set.size"+set.size()+" s="+s+" b="+test_add_tail(b));
        Person p = new Person("ly");
        p.name = "modify";
        Object test = (Object)p;
        test.toString();
        System.out.println("name = "+p.getName());
        if(p.getName().length()==8){
            p.setName("");
        }
        Integer i;

        Single.getInstance().say();

        return s;
    }

}


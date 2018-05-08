package com.ly.test;

/**
 * Created by liuyao-s on 2018/5/8.
 */

public class Single {
    private static Single instance;
    private Single(){

    }
    public static Single getInstance(){
        return instance;
    }
    public void say(){
        System.out.println("say something");
    }
}

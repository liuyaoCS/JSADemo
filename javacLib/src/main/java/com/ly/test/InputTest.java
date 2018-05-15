package com.ly.test;

import java.util.HashSet;

/**
 * Created by liuyao-s on 2018/5/10.
 */

public class InputTest {
    String name="";
    public void setName(String n){
        name = n;
    }
    public String test_method(HashSet set, int b) throws Exception {

        int a = 0;
        a = 1+2;

        InputTest test = new InputTest();
        test.name = "ss";
        setName("ly");

        Single.getInstance().say();

        InnerInput innerInput = test.new InnerInput();
        innerInput.pubsay();

        return "";
    }
    class InnerInput{
        public void pubsay(){
            System.out.println("pubsay");
        }
        private void prisay(){
            System.out.println("prisay");
        }
    }
}

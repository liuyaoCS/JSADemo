package com.ly.test;

import java.util.HashSet;

/**
 * Created by liuyao-s on 2018/5/10.
 */

public class InputTest {
    String name="";

    public String test_method(HashSet set, int b) throws Exception {

        int a = 0;
        a = 1+2;

        InputTest test = new InputTest();
        test.name = "ss";
        setName("ly");

        Single.getInstance().say();

        InnerInput innerInput = test.new InnerInput();
        innerInput.pubsay();

        new InnerInput(){
            @Override
            public void pubsay() {
                System.out.println(" anonymous pub say");
            }
        }.pubsay();

        InnerInput ip = new InnerInput(){
            @Override
            public void pubsay() {
                super.pubsay();
            }
        };
        ip.pubsay();

        class LocalInner{
            String li;
            void lim(){

            }
        }

        return "";
    }
    public void setName(String n){
        name = n;
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

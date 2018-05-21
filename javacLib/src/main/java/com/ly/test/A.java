package com.ly.test;

/**
 * Created by liuyao-s on 2018/5/14.
 */

public class A {
    private String a="a";
    void ma(){
        System.out.println("a="+a);
        //匿名内部类
        new AA(){
            @Override
            void maa() {
                super.maa();
                System.out.println("anonymous aa="+aa);
            }
        }.maa();
        new AA(){
            @Override
            void maa() {
                super.maa();
                System.out.println("anonymous aa="+aa);
            }
            void add(){
                System.out.println("anonymous add method,a="+A.this.a);
            }
        }.add();
        //局部内部类
        class LocalInner{
            String li = "local inner";
            void lm(){
                a = "local a";
                A.this.a = "how to use outer class object";
                System.out.println("local inner lm,a="+ a);
            }
        }
        LocalInner localInner = new LocalInner();
        localInner.lm();

        System.out.println("a="+a);
    }
    void ma2(){
        class LocalInner2{
            String li2 = "local inner";
            void lm2(){
                a = "local a";
                A.this.a = "how to use outer class object";
                System.out.println("local inner lm,a="+ a);
            }
        }
    }

    class AA{
        String aa="aa";
        void maa(){
            System.out.println("aa="+aa);
        }
        class AAA{
            String aaa="aaa";
            void maaa(){
                System.out.println("aaa="+aaa);
            }
        }
    }
    static class SAA{
        String saa="saa";
        void msaa(){
            System.out.println("saa="+saa);
        }
    }
}

package com.ly.test;

/**
 * Created by liuyao-s on 2018/5/14.
 */

public class A {
    int a;
    void ma(){
    }
    class AA{
        int aa;
        void maa(){
            ma();
        }
        class AAA{
            int aaa;
            void maaa(){
                aa =1;
                a = 1;
            }
        }
    }
}

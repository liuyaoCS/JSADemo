package com.ly.test;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.tools.JavaFileObject;

/**
 * Created by liuyao-s on 2018/5/4.
 */

public class Test {
    public static void main(String[] args)  {

        testAST();
        testInner();
        test();
    }
    static String className = "com.ly.test.A$AA";
    private static void test() {
        String pkg = "com.ly.test";
        // java "\\" stands "\" so -> pk.replaceAll("\.","\\");
        String ret = pkg.replaceAll("\\.", "\\\\");
        System.out.println("ret = "+ret);

        System.out.println("############");
        A a = new A();
        A.AA aa = a.new AA();

        try {
            Object ret1 = amendThiz(aa,"com.ly.test.A");
            if(ret1 instanceof A){
                System.out.println("success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void testAST() {
        String source = "F:\\as\\project\\JSADemo\\javacLib\\src\\main\\java\\com\\ly\\test\\InputTest.java";

        List<JCTree.JCCompilationUnit> trees = genASTWithSymbols(source);

        MethodVisitorTest mv = new MethodVisitorTest();
        JCTree.JCCompilationUnit unit = trees.get(0);

        List<JCTree> jtrees=unit.defs;
        for(JCTree jcTree:jtrees){
            if(jcTree instanceof JCTree.JCClassDecl){
                JCTree.JCClassDecl tmp = (JCTree.JCClassDecl) jcTree;
                List<JCTree> classTrees = tmp.defs;
                for(JCTree item:classTrees){
                    if(item instanceof JCTree.JCMethodDecl && ((JCTree.JCMethodDecl) item).getName().toString().equals("test_method")){
                        item.accept(mv,null);
                    }
                }
            }
        }
    }

    private static  Object  amendThiz(Object thiz, String clazzName) throws  Exception{
        int start = className.split("\\$").length-1;
        int dest = clazzName.split("\\$").length-1;
        if(start == dest){
            return thiz;
        }
        if(start < dest){
            return null;
        }

        String tmpClassName  = className;
        while(start > dest){
            start --;
            Class c1 = Class.forName(tmpClassName);
            Field f1 = c1.getDeclaredField("this$"+start);

            thiz = f1.get(thiz);
            int index = tmpClassName.lastIndexOf("$");
            tmpClassName = tmpClassName.substring(0,index);
        }
        return thiz;
    }

    private static void testInner() {
        Test test = new Test();
        Inner inner  = test.new Inner();
        inner.speak();

        Inner.Inner1 inner1 =inner.new Inner1();


        try {
            //1 newInstance(test)
            Class clazz  = Class.forName("com.ly.test.Test$Inner");
            Constructor[] constructor = clazz.getDeclaredConstructors();
            Object obj = constructor[0].newInstance(test);

            Method method = clazz.getDeclaredMethod("speak");
            method.setAccessible(true);
            method.invoke(obj);

            //2 this$0
            Field testF = clazz.getDeclaredField("this$0");
            Object testO = testF.get(inner);
            boolean ret = testO instanceof Test;
            System.out.println("ret = "+ret);

            Class inner1C = Class.forName("com.ly.test.Test$Inner$Inner1");
            Field field1 = inner1C.getDeclaredField("this$1");

            //3 Inner1 speak(Set<Inner>)
            Class[] clzs = new Class[]{Set.class};
            Set<Inner> sets = new HashSet<>();
            sets.add(inner);

            Object param = sets;
            Method speak1 = clazz.getDeclaredMethod("speak2",clzs);
            speak1.setAccessible(true);
            speak1.invoke(obj,param);

            //4 this$0 ths$1
            A a = new A();
            A.AA aa = a.new AA();
            A.AA.AAA aaa = aa.new AAA();

            Class aaaC = Class.forName("com.ly.test.A$AA$AAA");
            Field aaF = aaaC.getDeclaredField("this$1");
            Object aaO = aaF.get(aaa);
            System.out.println("aaO: "+(aaO instanceof A.AA)) ;

            Class aaC = Class.forName("com.ly.test.A$AA");
            Field aF = aaC.getDeclaredField("this$0");
            Object aO = aF.get(aaO);
            System.out.println("aO: "+(aO instanceof A)) ;


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception e:"+e.getMessage());
        }


    }

    private static List<JCTree.JCCompilationUnit> genASTWithSymbols(String filePath) {
        Context context = new Context();
        JavacFileManager jcFileManager = new JavacFileManager(context, true, Charset.defaultCharset());
        JavaCompiler comp = JavaCompiler.instance(context);

        List<JavaFileObject> fileObjects = List.nil();
        for(JavaFileObject jfo:jcFileManager.getJavaFileObjects(filePath)){
            fileObjects = fileObjects.prepend(jfo);
        }

        List<JCTree.JCCompilationUnit> trees = comp.parseFiles(fileObjects); //[step 1] gen ast
        trees = comp.enterTrees(trees);                                     //[step 2] add to symbol table
        comp = comp.processAnnotations(trees);                              //[step 3] process annotation
        comp.attribute(comp.todo);                                          //[step 4] finish symbol table

        return  trees;
    }
    class Inner{
        public Inner(){

        }
        void speak(){
            System.out.println("inner speak;");
        }
        void speak1(int a){
            System.out.println("inner speak; a="+a);
        }
        void speak2(Set<Inner> sets){
            System.out.println("inner speak; size="+sets.size());
        }
        class Inner1{

        }
    }

}

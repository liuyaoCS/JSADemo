package com.ly.test;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import java.nio.charset.Charset;

import javax.tools.JavaFileObject;

/**
 * Created by liuyao-s on 2018/5/4.
 */

public class Test {
    public static void main(String[] args){

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

}

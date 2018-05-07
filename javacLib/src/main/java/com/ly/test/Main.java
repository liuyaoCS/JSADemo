package com.ly.test;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.tools.JavaFileObject;

/**
 * Created by liuyao-s on 2018/4/23.
 */

public class Main {
    public static void main(String[] args){
        String source = "F:\\as\\project\\JSADemo\\javacLib\\src\\main\\java\\com\\ly\\test\\Input.java";
        String sourceNew = "F:\\as\\project\\JSADemo\\javacLib\\src\\main\\java\\com\\ly\\test\\InputNew.java";

        List<JCTree.JCCompilationUnit> trees = genASTWithSymbols(source,sourceNew);

//        processAST_dependences(trees);
        processAST(trees);
    }
    private static List<JCTree.JCCompilationUnit> genASTWithSymbols(String filePath,String filePathNew) {
        Context context = new Context();
        JavacFileManager jcFileManager = new JavacFileManager(context, true, Charset.defaultCharset());
        JavaCompiler comp = JavaCompiler.instance(context);

        List<JavaFileObject> fileObjects = List.nil();
        for(JavaFileObject jfo:jcFileManager.getJavaFileObjects(filePathNew,filePath)){
            fileObjects = fileObjects.prepend(jfo);
        }

        List<JCTree.JCCompilationUnit> trees = comp.parseFiles(fileObjects); //[step 1] gen ast
        trees = comp.enterTrees(trees);                                     //[step 2] add to symbol table
        comp = comp.processAnnotations(trees);                              //[step 3] process annotation
        comp.attribute(comp.todo);                                          //[step 4] finish symbol table

        return  trees;
    }
    private static void processAST_dependences(List<JCTree.JCCompilationUnit> trees){
        Set<String> imports = new HashSet<>();
        Set<String> requiredImports = new HashSet<>();

        JCTree.JCCompilationUnit unit = trees.get(0);
        List<JCTree> jtrees=unit.defs;
        for(JCTree jcTree:jtrees){
            if(jcTree instanceof JCTree.JCClassDecl){
                JCTree.JCClassDecl tmp = (JCTree.JCClassDecl) jcTree;
                List<JCTree> classTrees = tmp.defs;
                for(JCTree item:classTrees){
                    if(item instanceof JCTree.JCMethodDecl){
                        JCTree.JCMethodDecl methodTree = (JCTree.JCMethodDecl) item;

                        List<JCTree.JCAnnotation> annotations = methodTree.mods.annotations;
                        for(JCTree.JCAnnotation annotation:annotations){
                            System.out.println("annotation type="+annotation);
                        }

                        List<JCTree.JCStatement> stats = methodTree.body.stats;
                        for(JCTree.JCStatement jcStatement:stats){
                            if(jcStatement instanceof JCTree.JCVariableDecl){
                                Symbol.TypeSymbol dts = ((JCTree.JCVariableDecl) jcStatement).vartype.type.tsym;
                                System.out.println("dec type="+dts.toString());
                                if(imports.contains(dts.toString())){
                                    requiredImports.add(dts.toString());
                                }

                                Symbol.TypeSymbol ts = ((JCTree.JCVariableDecl) jcStatement).init.type.tsym;
                                System.out.println("init type="+ts.toString());
                                if(imports.contains(ts.toString())){
                                    requiredImports.add(ts.toString());
                                }
                            }
                        }
                        //just test visit method
                        item.accept(new MethodVisitor(),null);
                    }
                }
            }else if(jcTree instanceof JCTree.JCImport){
                JCTree.JCImport tmp = (JCTree.JCImport) jcTree;
                imports.add(tmp.qualid.toString());
            }
        }

        for(String importItem:requiredImports){
            System.out.println("required import :"+importItem);
        }
    }
    private static void processAST(List<JCTree.JCCompilationUnit> trees){

        List<JCTree.JCMethodDecl> list1 = fetchMethods(trees.get(0));
        List<JCTree.JCMethodDecl> list2 = fetchMethods(trees.get(1));

        //real process suppose only method modify and list1.size()==list2.size()
        for(int i=0;i<list1.size();i++){
            Type type = parseType(list1.get(i), list2.get(i));
            System.out.println("method1:"+list1.get(i).getName()+" method2:"+list2.get(i).getName()+" type:"+type);
        }

    }

    private static List<JCTree.JCMethodDecl> fetchMethods(JCTree.JCCompilationUnit unit) {
        List<JCTree.JCMethodDecl> list= List.nil();

        List<JCTree> jtrees=unit.defs;
        for(JCTree jcTree:jtrees){
            if(jcTree instanceof JCTree.JCClassDecl){
                JCTree.JCClassDecl tmp = (JCTree.JCClassDecl) jcTree;
                List<JCTree> classTrees = tmp.defs;
                for(JCTree item:classTrees){
                    if(item instanceof JCTree.JCMethodDecl){
                        JCTree.JCMethodDecl methodTree = (JCTree.JCMethodDecl) item;
                        list=list.append(methodTree);
                    }
                }
            }
        }
        return list;
    }

    private static Type parseType(JCTree.JCMethodDecl methodDecl1, JCTree.JCMethodDecl methodDecl2) {
        Type type =Type.REPLACE;

        List<JCTree.JCStatement> sts1 = methodDecl1.body.stats;
        List<JCTree.JCStatement> sts2 = methodDecl2.body.stats;

        int j=0;
        while(sts2.get(j).toString().equals(sts1.get(j).toString())
                || sts1.get(j).getKind().equals(Tree.Kind.RETURN)
                || sts2.get(j).getKind().equals(Tree.Kind.RETURN)){
            j++;
            if(j==sts1.length() || j==sts2.length())break;
        }
        if(j>0){
            if(j==sts1.length()){
                if(j==sts2.length()){
                    type= Type.PASS;
                }else if(j<sts2.length()){
                    type= Type.ADD_TAIL;
                }
            }
            return type;
        }

        int m=sts1.length()-1,n=sts2.length()-1;
        while(sts2.get(n).toString().equals(sts1.get(m).toString())
                || sts1.get(n).getKind().equals(Tree.Kind.RETURN)){
            m--;
            n--;
            if(m==-1 || n==-1)break;
        }
        if(m<sts1.length()-1) {
            if(m==-1){
                if(n>-1){
                    type= Type.ADD_HEAD;
                }
            }
        }else if(m==sts1.length()-1){
            type= Type.REPLACE;
        }

        return type;
    }

    private enum Type{
        REPLACE,
        ADD_HEAD,
        ADD_TAIL,
        PASS
    }

}

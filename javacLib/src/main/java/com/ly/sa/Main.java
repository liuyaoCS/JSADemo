package com.ly.sa;

import com.sun.source.tree.Tree;

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
        String source = "F:\\as\\project\\JSADemo\\javacLib\\src\\main\\java\\com\\ly\\sa\\Input.java";
        String sourceNew = "F:\\as\\project\\JSADemo\\javacLib\\src\\main\\java\\com\\ly\\sa\\InputNew.java";

        List<JCTree.JCCompilationUnit> trees = genASTWithSymbols(source,sourceNew);


        System.out.println("\n#########test method Input.test_replace##############");
        processAST_dependences(trees);

        System.out.println("\n##########test parse before after##############");
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

        MethodVisitor mv = new MethodVisitor();
        JCTree.JCCompilationUnit unit = trees.get(0);

        List<JCTree> jtrees=unit.defs;
        for(JCTree jcTree:jtrees){
            if(jcTree instanceof JCTree.JCClassDecl){
                JCTree.JCClassDecl tmp = (JCTree.JCClassDecl) jcTree;
                List<JCTree> classTrees = tmp.defs;
                for(JCTree item:classTrees){
                    if(item instanceof JCTree.JCMethodDecl
                            && ((JCTree.JCMethodDecl) item).getName().toString().equals("test_replace")){
                        item.accept(mv,null);
                    }
                }
            }
        }

        for(String str:mv.getRequireImports()){
            System.out.println("possible import:"+str);
        }

    }
    private static void processAST(List<JCTree.JCCompilationUnit> trees){

        List<JCTree.JCMethodDecl> list1 = fetchMethods(trees.get(0));
        List<JCTree.JCMethodDecl> list2 = fetchMethods(trees.get(1));

        //real process suppose only method modify and list1.size()==list2.size()
        for(int i=0;i<list1.size();i++){
            Type type = parseType(list1.get(i), list2.get(i));
            System.out.println("method1:"+list1.get(i).getName()+" method2:"+list2.get(i).getName()+" type:"+type);
            invokeMethod(list1.get(i),list2.get(i),type);
        }

    }

    private static void invokeMethod(JCTree.JCMethodDecl method1, JCTree.JCMethodDecl method2, Type type) {
        switch (type){
            case PASS:
            case REPLACE:
                break;
            case ADD_HEAD:
                int pos = type.pos+1;
                StringBuilder sb=new StringBuilder();

                StringBuilder argsStr = new StringBuilder();
                List<JCTree.JCVariableDecl> paras = method2.getParameters();
                int parasSize = paras.size();
                for(int i=0;i<parasSize;i++){
                    String str = paras.get(i).vartype + " " + paras.get(i).name+" = ("+paras.get(i).vartype+")"+"args["+i+"];\n";
                    argsStr.append(str);

                }
                sb.append(argsStr).append("\n");

                for(int i=0; i < pos;i++){
                    sb.append(method2.body.getStatements().get(i).toString());
                    sb.append("\n");
                }

                sb.append("\nObject[] rets = new Object[args.length()];\n");
                for(int i=0;i<parasSize;i++){
                    sb.append("rets["+ i +"]="+paras.get(i).name +";\n");
                }
                sb.append("return rets;\n");

                System.out.println("before:\n"+sb);
                break;
            case ADD_TAIL:
                int p = type.pos-1;
                List<JCTree.JCStatement> stats=method2.getBody().getStatements();
                StringBuilder sts=new StringBuilder();

                Set<String> vars1 = new HashSet<>();
                MethodVisitor methodVisitor = new MethodVisitor();
                method1.accept(methodVisitor,null);
                vars1.addAll(methodVisitor.getVars());

                Set<String> vars2 = new HashSet<>();
                BlockVisitor blockVisitor =new BlockVisitor();
                method2.body.accept(blockVisitor,p,null);
                vars2.addAll(blockVisitor.getVars());

                Set<String> vars = new HashSet<>();
                vars.addAll(vars1);
                vars.retainAll(vars2);
                if(vars.size()!=0){
                    System.out.println("can not process now!");
                    break;
                }else{
                    for(int i=p;i<stats.size();i++){
                        sts.append(stats.get(i)+"\n");
                    }
                    System.out.println("after:\n"+sts);
                }

                break;
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
                    type.pos=j;
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
                    type.pos=n;
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
        PASS;

        public int pos = -1;
    }

}

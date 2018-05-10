package com.ly.sa;

import com.ly.sa.utils.KMP;
import com.ly.sa.visitor.BlockVisitor;
import com.ly.sa.visitor.MethodVisitor;
import com.ly.sa.visitor.ReturnVisitor;
import com.sun.source.tree.Tree;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import java.nio.charset.Charset;
import javax.tools.JavaFileObject;

import static com.ly.sa.utils.ASTUtils.*;

/**
 * Created by liuyao-s on 2018/4/23.
 */

public class Main {
    public static void main(String[] args){
        String source = "F:\\as\\project\\JSADemo\\javacLib\\src\\main\\java\\com\\ly\\test\\input\\Input.java";
        String sourceNew = "F:\\as\\project\\JSADemo\\javacLib\\src\\main\\java\\com\\ly\\test\\input\\InputNew.java";

        List<JCTree.JCCompilationUnit> trees = genASTWithSymbols(source,sourceNew);


        System.out.println("\n#########test method Input.test_replace##############");
        processAST_dependences(trees);

        System.out.println("\n##########test parse before after##############");
        processAST_method(trees);
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
    private static void processAST_method(List<JCTree.JCCompilationUnit> trees){

        List<JCTree.JCMethodDecl> list1 = fetchMethods(trees.get(0));
        List<JCTree.JCMethodDecl> list2 = fetchMethods(trees.get(1));

        //real process suppose only method modify and list1.size()==list2.size()
        for(int i=0;i<list1.size();i++){
            JCTree.JCMethodDecl method1 = list1.get(i);
            JCTree.JCMethodDecl method2 = list2.get(i);
            Type type =Type.REPLACE;

            if(method1.toString().equals(method2.toString())){
                //[type pass]
                System.out.println("\n[pass]: "+method1.name);
                type = Type.PASS;
                continue;
            }
            if(method1.body.stats.size()==0 ||
                    (method1.body.stats.size()==1 && method1.body.stats.get(0).getKind().equals(Tree.Kind.RETURN))){
                //[type replace]
                System.out.println("\n[replace]: "+method1.name);
                type =Type.REPLACE;
                continue;
            }

            int pos = KMP.find(method2.body.stats,method1.body.stats);
            if(pos == -1){
                //[type replace]
                System.out.println("\n[replace]: "+method1.name);
                type =Type.REPLACE;
                continue;
            }


            //add before
            StringBuilder before = new StringBuilder();
            if(pos>0){
                StringBuilder argsStr = new StringBuilder();
                List<JCTree.JCVariableDecl> paras = method2.getParameters();
                int parasSize = paras.size();
                for(int j=0;j<parasSize;j++){
                    String str = paras.get(j).vartype + " " + paras.get(j).name+" = ("+paras.get(j).vartype+")"+"args["+j+"];\n";
                    argsStr.append(str);

                }
                before.append(argsStr).append("\n");

                for(int m=0;m<pos;m++){
                    String expression  = method2.body.stats.get(m).toString();
                    if(!expression.endsWith(";")){
                        expression=expression+";";
                    }
                    before.append(expression).append("\n");
                }

                before.append("\nObject[] rets = new Object[args.length()];\n");
                for(int k=0;k<parasSize;k++){
                    before.append("rets["+ k +"]="+paras.get(k).name +";\n");
                }
                before.append("return rets;\n");

                type = Type.ADD_HEAD;
            }

            //add after
            StringBuilder after = new StringBuilder();
            JCTree.JCStatement lastStat1 = method1.body.stats.last();
            JCTree.JCStatement lastStat2 = method2.body.stats.last();
            int start = pos+method1.body.stats.size();
            int end = method2.body.stats.size();
            if(lastStat1.getKind().equals(Tree.Kind.RETURN)){
                start --;
            }
            if(lastStat2.getKind().equals(Tree.Kind.RETURN)){
                end --;
            }
            if(start<end){
                MethodVisitor methodVisitor = new MethodVisitor();
                method1.accept(methodVisitor,null);

                BlockVisitor blockVisitor =new BlockVisitor();
                method2.body.accept(blockVisitor,start,end-1,null);

                if(!canAddAfter(methodVisitor,blockVisitor)){
                    System.out.println("\n[replace]: "+method1.name);
                    continue;
                }

                for(int n=start;n<end;n++){
                    String expression  = method2.body.stats.get(n).toString();
                    if(!expression.endsWith(";")){
                        expression=expression+";";
                    }
                    after.append(expression).append("\n");
                }

                if(type == Type.ADD_HEAD){
                    type = Type.ADD_BOTH;
                }else{
                    type = Type.ADD_TAIL;
                }
            }

            //check return : must be one of add_head add_tail add_both
            ReturnVisitor returnVisitor1 = new ReturnVisitor();
            lastStat1.accept(returnVisitor1,null);
            List<JCTree.JCMethodInvocation> jcMethodInvocations1 = returnVisitor1.getJcMethodInvocations();
            ReturnVisitor returnVisitor2 = new ReturnVisitor();
            lastStat2.accept(returnVisitor2,null);
            List<JCTree.JCMethodInvocation> jcMethodInvocations2 = returnVisitor2.getJcMethodInvocations();

            if(lastStat1.getKind().equals(Tree.Kind.RETURN) && jcMethodInvocations1.size()>0){
                if(!lastStat2.getKind().equals(Tree.Kind.RETURN) ||
                        !(type == Type.ADD_HEAD && jcMethodInvocations1==jcMethodInvocations2)) {
                    System.out.println("\n[replace]: "+method1.name);
                    continue;
                }
            }
            if(lastStat2.getKind().equals(Tree.Kind.RETURN)){
                after.append(lastStat2);
            }

            //invoke method
            switch (type){
                case ADD_HEAD:
                    System.out.println("\n[add_head]:"+method1.name);
                    System.out.println("before:\n"+before);
                    break;
                case ADD_TAIL:
                    System.out.println("\n[add_tail]:"+method1.name);
                    System.out.println("after:\n"+after);
                    break;
                case ADD_BOTH:
                    System.out.println("\n[add_both]:"+method1.name);
                    System.out.println("before:\n"+before);
                    System.out.println("after:\n"+after);
                    break;
                default:
                    System.out.println("\n[type err]");
                    break;
            }
        }
    }

}

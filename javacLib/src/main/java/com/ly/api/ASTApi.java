package com.ly.api;

import com.ly.sa.utils.ASTUtils;
import com.ly.sa.utils.KMP;
import com.ly.sa.visitor.BlockVisitor;
import com.ly.sa.visitor.MethodVisitor;
import com.ly.sa.visitor.ReturnVisitor;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import static com.ly.sa.utils.ASTUtils.canAddAfter;

/**
 * Created by liuyao-s on 2018/5/21.
 */

public class ASTApi {
    public static ParseResult parseMethod(JCTree.JCMethodDecl method1, JCTree.JCMethodDecl method2) {
        ParseResult parseResult = new ParseResult();
        parseResult.type = ASTUtils.Type.REPLACE;

        if(method1.toString().equals(method2.toString())){
            //[type pass]
            System.out.println("\n[pass]: "+method1.name);
            parseResult.type = ASTUtils.Type.PASS;
            return parseResult;
        }
        if(method1.body.stats.size()==0 ||
                (method1.body.stats.size()==1 && method1.body.stats.get(0).getKind().equals(Tree.Kind.RETURN))){
            //[type replace]
            System.out.println("\n[replace]: "+method1.name);
            parseResult.type = ASTUtils.Type.REPLACE;
            return parseResult;
        }

        int pos = KMP.find(method2.body.stats,method1.body.stats);
        if(pos == -1){
            //[type replace]
            System.out.println("\n[replace]: "+method1.name);
            parseResult.type = ASTUtils.Type.REPLACE;
            return parseResult;
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

            parseResult.type = ASTUtils.Type.ADD_HEAD;
            parseResult.before = pos;
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
                parseResult.type = ASTUtils.Type.REPLACE;
                parseResult.before = -1;
                parseResult.after = -1;
                return parseResult;
            }

            for(int n=start;n<end;n++){
                String expression  = method2.body.stats.get(n).toString();
                if(!expression.endsWith(";")){
                    expression=expression+";";
                }
                after.append(expression).append("\n");
            }

            if(parseResult.type == ASTUtils.Type.ADD_HEAD){
                parseResult.type = ASTUtils.Type.ADD_BOTH;
            }else{
                parseResult.type = ASTUtils.Type.ADD_TAIL;
            }
            parseResult.after = start;
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
                    !(parseResult.type == ASTUtils.Type.ADD_HEAD && jcMethodInvocations1==jcMethodInvocations2)) {
                System.out.println("\n[replace]: "+method1.name);
                parseResult.type = ASTUtils.Type.REPLACE;
                parseResult.before = -1;
                parseResult.after = -1;
                return parseResult;
            }
        }
        if(lastStat2.getKind().equals(Tree.Kind.RETURN)){
            after.append(lastStat2);
        }

        //invoke method
        switch (parseResult.type){
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

        return parseResult;
    }
}

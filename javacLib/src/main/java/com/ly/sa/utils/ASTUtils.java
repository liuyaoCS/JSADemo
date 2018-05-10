package com.ly.sa.utils;

import com.ly.sa.visitor.BlockVisitor;
import com.ly.sa.visitor.MethodVisitor;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuyao-s on 2018/5/10.
 */

public class ASTUtils {
    public enum Type{
        REPLACE,
        ADD_HEAD,
        ADD_TAIL,
        ADD_BOTH,
        PASS;

        @Deprecated
        public int pos = -1;
    }

    public static boolean canAddAfter(MethodVisitor methodVisitor, BlockVisitor blockVisitor){
        Set<String> vars = methodVisitor.getVars();
        Set<String> assigns = methodVisitor.getAssigns();
        Set<String> assignOps = methodVisitor.getAssignOps();
        Set<String> unaries = methodVisitor.getUnaries();
        Set<String> fieldAccesses = methodVisitor.getFieldAccesses();
        Set<String> all = new HashSet<>();
        all.addAll(assigns);
        all.addAll(assignOps);
        all.addAll(unaries);
        all.addAll(fieldAccesses);

        Set<String> syms = blockVisitor.getSyms();
        for(String sym: syms){
            if(vars.contains(sym)){
                for(String item: all){
                    if(item.contains(sym)){
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public static List<JCTree.JCMethodDecl> fetchMethods(JCTree.JCCompilationUnit unit) {
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

    @Deprecated
    public static Type parseType(JCTree.JCMethodDecl methodDecl1, JCTree.JCMethodDecl methodDecl2) {
        Type type = Type.REPLACE;

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
}

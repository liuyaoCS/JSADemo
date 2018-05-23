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

}

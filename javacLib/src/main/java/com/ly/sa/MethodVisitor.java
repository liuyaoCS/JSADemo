package com.ly.sa;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuyao-s on 2018/5/3.
 */

public class MethodVisitor extends TreeScanner {

    private Set<String> requireImports = new HashSet<>();
    public Set<String> getRequireImports() {
        return requireImports;
    }
    private boolean ignoreImport(String tsym){
        tsym = tsym.replace("java.lang.","");
        return !tsym.contains(".");
    }

    private Set<String> vars= new HashSet<>();
    public Set<String> getVars(){
        return vars;
    }

    @Override
    public Object visitIdentifier(IdentifierTree node, Object o) {
//        System.out.println("visitIdentifier:"+node.toString());
        JCTree.JCIdent jcIdent = (JCTree.JCIdent) node;
        String tsym =  jcIdent.type.tsym.toString();
        if(!ignoreImport(tsym)){
            requireImports.add(tsym);
        }
        return super.visitIdentifier(node, o);
    }


    @Override
    public Object visitVariable(VariableTree node, Object o) {
//        System.out.println("MethodVisitor visitVariable:"+node.toString());
        JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) node;
        String  var = jcVariableDecl.name.toString();
        vars.add(var);
        return super.visitVariable(node, o);
    }
}

package com.ly.sa.visitor;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuyao-s on 2018/5/3.
 */

public class BlockVisitor extends TreeScanner {

    private Set<String> ovars= new HashSet<>();
    public void setOutVars(Set<String> vars){
        ovars = vars;
    }

    private boolean proccessRet=true;
    public boolean getRet(){
        return proccessRet;
    }

    private Set<String> syms = new HashSet<>();
    public Set<String> getSyms() {
        return syms;
    }

    @Override
    public Object visitIdentifier(IdentifierTree node, Object o) {
//        System.out.println("BlockVisitor visitIdentifier : "+node.toString());
        Symbol symbol = ((JCTree.JCIdent)node).sym;
//        if(ovars.contains(symbol.toString())){
//            System.out.println("using symbol " + symbol.toString() );
//            proccessRet = false;
//            return null;
//        }
        syms.add(symbol.toString());
        return super.visitIdentifier(node, o);
    }

}

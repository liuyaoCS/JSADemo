package com.ly.sa;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuyao-s on 2018/5/3.
 */

public class BlockVisitor extends TreeScanner {
    Set<String> vars= new HashSet<>();
    public Set<String> getVars(){
        return vars;
    }

    @Override
    public Object visitIdentifier(IdentifierTree node, Object o) {
//        System.out.println("BlockVisitor visitIdentifier : "+node.toString());
        Symbol symbol = ((JCTree.JCIdent)node).sym;
        if(symbol instanceof  Symbol.VarSymbol){
            vars.add(symbol.toString());
        }
        return super.visitIdentifier(node, o);
    }

}

package com.ly.sa.visitor;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuyao-s on 2018/5/3.
 */

public class ReturnVisitor extends TreeScanner {
    private List<JCTree.JCMethodInvocation> jcMethodInvocations = List.nil();
    public List<JCTree.JCMethodInvocation> getJcMethodInvocations(){
        return jcMethodInvocations;
    }
    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Object o) {
        JCTree.JCMethodInvocation jcMethodInvocation = (JCTree.JCMethodInvocation) node;
        jcMethodInvocations.add(jcMethodInvocation);
        return super.visitMethodInvocation(node, o);
    }

    @Override
    public Object visitIdentifier(IdentifierTree node, Object o) {
        return super.visitIdentifier(node, o);
    }
}

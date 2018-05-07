package com.ly.test;

//import com.sun.tools.javac.tree.TreeScanner;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;

/**
 * Created by liuyao-s on 2018/5/3.
 */

public class MethodVisitor extends TreeScanner {
    @Override
    public Object visitAssignment(AssignmentTree node, Object o) {
        System.out.println("visitAssignment:"+node.toString());
        return super.visitAssignment(node, o);
    }

    @Override
    public Object visitMemberSelect(MemberSelectTree node, Object o) {
        System.out.println("visitMemberSelect:"+node.toString());
        return super.visitMemberSelect(node, o);
    }

    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Object o) {
        System.out.println("visitMethodInvocation:"+node.toString());
        return super.visitMethodInvocation(node, o);
    }

    @Override
    public Object visitMethod(MethodTree node, Object o) {
        System.out.println("visitMethod:"+node.toString());
        return super.visitMethod(node, o);
    }

    @Override
    public Object visitBlock(BlockTree node, Object o) {
        return super.visitBlock(node, o);
    }
}

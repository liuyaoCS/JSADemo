package com.ly.test;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuyao-s on 2018/5/3.
 */

public class MethodVisitorTest extends TreeScanner {


    //=
    @Override
    public Object visitAssignment(AssignmentTree node, Object o) {
        System.out.println("visitAssignment:"+node.toString());
        return super.visitAssignment(node, o);
    }
    //+=
    @Override
    public Object visitCompoundAssignment(CompoundAssignmentTree node, Object o) {
//        System.out.println("visitCompoundAssignment:"+node.toString());
        return super.visitCompoundAssignment(node, o);
    }
    //++
    @Override
    public Object visitUnary(UnaryTree node, Object o) {
//        System.out.println("visitUnary:"+node.toString());
        return super.visitUnary(node, o);
    }
    //.()  --filter-- Type.MethodType
    @Override
    public Object visitMemberSelect(MemberSelectTree node, Object o) {
        System.out.println("visitMemberSelect:"+node.toString());
        return super.visitMemberSelect(node, o);
    }


    @Override
    public Object visitIdentifier(IdentifierTree node, Object o) {
//        System.out.println("visitIdentifier:"+node.toString());

        return super.visitIdentifier(node, o);
    }

    @Override
    public Object visitVariable(VariableTree node, Object o) {
//        System.out.println("visitVariable:"+node.toString());

        return super.visitVariable(node, o);
    }

    @Override
    public Object visitMethodInvocation(MethodInvocationTree node, Object o) {
        System.out.println("visitMethodInvocation:"+node.toString());
        return super.visitMethodInvocation(node, o);
    }
}

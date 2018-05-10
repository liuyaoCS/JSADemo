package com.ly.sa.visitor;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Type;
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

    private Set<String> assigns= new HashSet<>();
    public Set<String> getAssigns() {
        return assigns;
    }

    private Set<String> assignOps = new HashSet<>();
    public Set<String> getAssignOps() {
        return assignOps;
    }

    private Set<String> unaries = new HashSet<>();
    public Set<String> getUnaries() {
        return unaries;
    }

    private Set<String> fieldAccesses = new HashSet<>();
    public Set<String> getFieldAccesses() {
        return fieldAccesses;
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

    //=
    @Override
    public Object visitAssignment(AssignmentTree node, Object o) {
//        System.out.println("visitAssignment:"+node.toString());
        JCTree.JCAssign assign = (JCTree.JCAssign) node;
        assigns.add(assign.lhs.toString());
        return super.visitAssignment(node, o);
    }
    //+=
    @Override
    public Object visitCompoundAssignment(CompoundAssignmentTree node, Object o) {
//        System.out.println("visitCompoundAssignment:"+node.toString());
        JCTree.JCAssignOp assignOp = (JCTree.JCAssignOp) node;
        assignOps.add(assignOp.lhs.toString());
        return super.visitCompoundAssignment(node, o);
    }
    //++
    @Override
    public Object visitUnary(UnaryTree node, Object o) {
//        System.out.println("visitUnary:"+node.toString());
        JCTree.JCUnary unary = (JCTree.JCUnary) node;
        unaries.add(unary.arg.toString());
        return super.visitUnary(node, o);
    }
    //.()  --filter-- MethodType
    @Override
    public Object visitMemberSelect(MemberSelectTree node, Object o) {
//        System.out.println("visitMemberSelect:"+node.toString());
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) node;
        if(fieldAccess.type.tsym.toString().equals("Method")){
            fieldAccesses.add(fieldAccess.selected.toString());
        }
        return super.visitMemberSelect(node, o);
    }
}

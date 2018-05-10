package com.ly.sa.utils;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;

/**
 * Created by liuyao-s on 2018/5/9.
 */

public class KMP {
    private static int[] next;
    private static void fixNext(List<? extends JCTree> p){
        next=new int[p.size()];
        int i=0,j=-1;
        next[0]=-1;
        while(i<p.size()){
            if(j==-1 || p.get(i).toString().equals(p.get(j).toString())){
                i++;
                j++;
                if(i<p.size())next[i]=j;
            }else j=next[j];
        }
    }
    private static int findInner(List<? extends JCTree> s, List<? extends JCTree> p){
        fixNext(p);
        int i=0,j=0;
        while(i<s.size() && j<p.size()){
            if(j==-1 || s.get(i).toString().equals(p.get(j).toString()) ){
                i++;
                j++;
            }else j=next[j];
        }
        if(j==p.size())return i-j;
        else return -1;
    }
    public static int find(List<? extends JCTree> s, List<? extends JCTree> p){
        List<? extends JCTree> sT = null;
        List<? extends JCTree> pT = null;
        if(s.get(s.size()-1).getKind().equals(Tree.Kind.RETURN)){
            sT = s.subList(0,s.size()-1);
        }
        if(p.get(p.size()-1).getKind().equals(Tree.Kind.RETURN)){
            pT = p.subList(0,p.size()-1);
        }
        return findInner(sT!=null?sT:s,pT!=null?pT:p);
    }

}

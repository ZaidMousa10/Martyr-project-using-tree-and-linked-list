package com.example.secondproject;


public class TNode<T extends Comparable<T>> implements Comparable<TNode<T>> {

    T data;
    TNode<T> left;
    TNode<T> right;

    public TNode(T data) { this.data = data; }

    public void setData(T data) { this.data=data; }
    public T getData() { return data; }

    public TNode getLeft() { return left; }
    public void setLeft(TNode left) { this.left = left; }
    public TNode getRight() { return right; }
    public void setRight(TNode right) { this.right = right;}

    public boolean isLeaf(){ return (left==null && right==null); }
    public boolean hasLeft(){ return left!=null; }
    public boolean hasRight(){ return right!=null; }

    @Override
    public String toString() { return  data +"" ; }


    public int compare(T data){
        return this.data.compareTo(data);
    }

    @Override
    public int compareTo(TNode<T> o) {
        return this.data.compareTo(o.data);
    }
}

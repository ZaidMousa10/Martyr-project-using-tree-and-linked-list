package com.example.secondproject;

public class SNode <T extends Comparable<T>>{
    private T data;
    private SNode<T> next;

    public SNode(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public SNode<T> getNext() {
        return next;
    }

    public void setNext(SNode<T> next) {
        this.next = next;
    }

    public int compare(T data){
        return this.data.compareTo(data);
    }


    @Override
    public String toString() {
        return "SNode{" +
                "data=" + data +
                ", next=" + next +
                '}';
    }
}

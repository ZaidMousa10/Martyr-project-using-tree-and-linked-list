package com.example.secondproject;

public class SLinkedList <T extends Comparable<T>>  {
    private SNode<T> head;
    private SNode<T> tail;


    public SLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public void insertFirst(T data) {
        SNode<T> newNode = new SNode<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }
    }
    public void insertLast(T data) {
        SNode<T> newNode = new SNode<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
    }


    public void insert(T data) {
        SNode<T> newNode = new SNode<>(data);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            return;
        }

        SNode<T> current = head;
        SNode<T> previous = null;

        while (current != null && current.compare(data) < 0) {
            previous = current;
            current = current.getNext();
        }

        if (previous == null) { // Insert at the beginning
            newNode.setNext(head);
            head = newNode;
        } else if (current == null) { // Insert at the end
            previous.setNext(newNode);
            tail = newNode;
        } else { // Insert in the middle
            newNode.setNext(current);
            previous.setNext(newNode);
        }
    }


    public T get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative.");
        }

        SNode<T> current = head;
        int currentIndex = 0;

        while (current != null && currentIndex < index) {
            current = current.getNext();
            currentIndex++;
        }

        if (current == null) {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }

        return current.getData();
    }

    public boolean deleteFirst() {
        if (isEmpty()) {
            return false;
        }

        head = head.getNext();
        if (head == null) {
            tail = null; // No more nodes in the list
        }

        return true; // Deletion successful
    }
    public boolean deleteLast() {
        if (isEmpty()) {
            return false;
        }

        if (head == tail) {
            head = null;
            tail = null;
            return true;
        }

        SNode<T> current = head;
        while (current.getNext() != tail) {
            current = current.getNext();
        }

        current.setNext(null);
        tail = current;

        return true; // Deletion successful
    }

    public boolean find(T data) {
        SNode<T> current = head;

        while (current != null) {
            if (current.getData().equals(data)) {
                return true;
            }
            current = current.getNext();
        }

        return false; // Not found
    }

    // O(n)
    public void traverse() {
        SNode<T> current = head;

        while (current != null) {
            System.out.print(current.getData() + " ");
            current = current.getNext();
        }

        System.out.println("null");
    }

    //O(n)
    public int length() {
        int length = 0;
        SNode<T> current = head;

        while (current != null) {
            length++;
            current = current.getNext();
        }

        return length;
    }

    //O(1)
    public boolean isEmpty() {
        return head == null;
    }
    public boolean contains(T data) {
        SNode<T> current = head;

        while (current != null) {
            if (current.getData().equals(data)) {
                return true; // Data found
            }
            current = current.getNext();
        }

        return false; // Data not found
    }

    public void clear() {
        head = null;
        tail = null;
    }

    public SNode<T> getHead() {
        return head;
    }

    public SNode<T> getTail() {
        return tail;
    }

    public void setHead(SNode<T> head) {
        this.head = head;
    }
    public void setTail(SNode<T> tail) {
        this.tail = tail;
    }


}

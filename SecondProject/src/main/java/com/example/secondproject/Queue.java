package com.example.secondproject;

import java.util.EmptyStackException;

public class Queue<T extends Comparable<T>> {
    private SLinkedList<T> list;

    public Queue() {
        this.list = new SLinkedList<>();
    }

    // Enqueue operation: O(1) time complexity
    public void enqueue(T data) {
        list.insertLast(data); // Insert at the end of the linked list
    }

    // Dequeue operation: O(1) time complexity
    public T dequeue() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = list.getHead().getData();
        list.deleteFirst();
        return data;
    }

    // Peek operation: O(1) time complexity
    public T getFront() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.getHead().getData();
    }

    // Check if the queue is empty: O(1) time complexity
    public boolean isEmpty() {
        return list.isEmpty();
    }

    // Get the size of the queue: O(n) time complexity
    public int size() {
        return list.length();
    }

    // Clear the queue: O(1) time complexity
    public void clear() {
        list.clear();
    }

    @Override
    public String toString() {
        return "Queue{" +
                  list +
                '}';
    }
}
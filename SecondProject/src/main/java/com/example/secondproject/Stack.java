package com.example.secondproject;
import java.util.EmptyStackException;

public class Stack<T extends Comparable<T>> {
    private Queue<T> queue;

    public Stack() {
        this.queue = new Queue<>();
    }

    // Push operation: O(n) time complexity (due to recursive nature)
    public void push(T data) {
        queue.enqueue(data); // Enqueue the new element
    }

    // Pop operation: O(n) time complexity (due to recursive nature)
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }

        // Move all elements except the last one to a temporary queue
        Queue<T> tempQueue = new Queue<>();
        while (queue.size() > 1) {
            tempQueue.enqueue(queue.dequeue());
        }

        // Dequeue and return the last element (simulating stack behavior)
        T poppedElement = queue.dequeue();

        // Restore elements back to the original queue
        while (!tempQueue.isEmpty()) {
            queue.enqueue(tempQueue.dequeue());
        }

        return poppedElement;
    }

    // Peek operation: O(n) time complexity (due to recursive nature)
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }

        // Move all elements except the last one to a temporary queue
        Queue<T> tempQueue = new Queue<>();
        while (queue.size() > 1) {
            tempQueue.enqueue(queue.dequeue());
        }

        // Peek at the last element (simulating stack behavior)
        T topElement = queue.dequeue();
        tempQueue.enqueue(topElement);

        // Restore elements back to the original queue
        while (!tempQueue.isEmpty()) {
            queue.enqueue(tempQueue.dequeue());
        }

        return topElement;
    }

    // Check if the stack is empty: O(1) time complexity
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Get the size of the stack: O(1) time complexity
    public int size() {
        return queue.size();
    }

    // Clear the stack: O(1) time complexity
    public void clear() {
        queue.clear();
    }
}

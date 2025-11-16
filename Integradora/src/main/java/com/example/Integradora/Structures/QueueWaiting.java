package com.example.Integradora.Structures;

import com.example.Integradora.Model.Node;

public class QueueWaiting<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public QueueWaiting() {
        head = tail = null;
        size = 0;
    }

    public boolean enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
        return true;
    }

    public T dequeue() {
        if (isEmpty()) { return null; }
        T value = this.head.getData();
        head = head.getNext();
        size--;
        if(isEmpty()) { tail = null; }
        return value;
    }

    public T peek() {
        if (isEmpty()) { return null; }
        return head.getData();
    }

    public boolean clear() {
        head = tail = null;
        size = 0;
        return true;
    }

    public boolean isEmpty() {
        return head == null;
    }
    
    public int getSize() {
        return size;
    }
    
    public Node<T> getHead() {
        return head;
    }
}

package com.example.Integradora.Structures;

import com.example.Integradora.Model.Node;

public class ParkingLotList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;
    private int sizeLimit;

    public ParkingLotList(int sizeLimit) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.sizeLimit = sizeLimit;
    }

    public T add(T data) {
        if (size >= sizeLimit) {
            return null;
        } else {
            Node<T> newNode = new Node<>(data);
            if (isEmpty()){
                head = tail = newNode;
            } else {
                tail.setNext(newNode);
                tail = newNode;
            }
            size++;
            return data;
        }
    }

    public T remove(T data) {
        if (isEmpty()){ return null; }

        T removed;
        if(head.getData().equals(data)){
            removed = head.getData();
            head = head.getNext();
            size--;
            if(isEmpty()){tail = null;}
            return removed;
        }

        Node<T> current = head;
        while (current.getNext() != null) {
            if (current.getNext().getData().equals(data)) {
                removed = current.getNext().getData();
                if (current.getNext() == tail) { tail = current; }
                current.setNext(current.getNext().getNext());
                size--;
                return removed;
            }
            current = current.getNext();
        }
        return null;
    }

    public boolean isFull() {
        return size == sizeLimit;
    }

    public T getData(T data) {
        if(isEmpty()){ return null; }

        if(head.getData().equals(data)){
            return head.getData();
        }

        Node<T> current = head;
        while(current.getNext() != null){
            if(current.getNext().getData().equals(data)){
                return current.getNext().getData();
            }
            current = current.getNext();
        }
        return null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public Node<T> getHead() {
        return head;
    }

    public void setHead(Node<T> head) {
        this.head = head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public void setTail(Node<T> tail) {
        this.tail = tail;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }
}

package com.example.Integradora.Structures;

import com.example.Integradora.Model.Node;

public class TicketList <T>{
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public TicketList(){
        head = tail = null;;
        size = 0;
    }

    public T add(T data) {
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

    public boolean isEmpty() {
        return head == null;
    }
}

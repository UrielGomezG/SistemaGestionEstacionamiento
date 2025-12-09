package com.example.Integradora.Structures;

import com.example.Integradora.Model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de datos Pila (Stack) implementada manualmente.
 * Sigue el principio LIFO (Last In, First Out).
 * Se utiliza para registrar el historial de salidas del estacionamiento.
 * 
 * @param <T> Tipo de dato a almacenar
 */
public class StackHistory<T> {
    private Node<T> top;
    private int size;

    public StackHistory() {
        top = null;
        size = 0;
    }

    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.setNext(top);
        top = newNode;
        size++;
    }

    public T pop() {
        if (isEmpty())
            return null;
        T data = top.getData();
        top = top.getNext();
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty())
            return null;
        return top.getData();
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    public List<T> showAll() {
        List<T> result = new ArrayList<>();
        Node<T> current = top;
        while (current != null) {
            result.add(current.getData());
            current = current.getNext();
        }
        return result;
    }
}
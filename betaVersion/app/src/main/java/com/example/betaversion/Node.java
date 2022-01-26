package com.example.betaversion;

import java.util.*;


public class Node<T> {
    private T element;
    private Node<T> next;

    public Node(T e, Node<T> n) {
        element = e;
        next = n;
    }

    public T getElement() {
        return element;
    }
    public void setElement(T e) {
         this.element = e;
    }

    public ArrayList<T> toArraylist()
    {
        ArrayList<T> arr = new ArrayList<T>();

        Node<T> ptr = this;

        while (ptr != null)
        {
            arr.add(ptr.getElement());
            ptr = ptr.getNext();
        }

        return arr;

    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> n) {
        next = n;
    }
}
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

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> n) {
        next = n;
    }
}
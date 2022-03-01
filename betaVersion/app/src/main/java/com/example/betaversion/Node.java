package com.example.betaversion;

import java.util.*;


public class Node<T> {
    private T element;
    private Node<T> next;

    public Node(T e, Node<T> n) {
        element = e;
        next = n;
    }

    public static Node<Note> castFromStringToNote(String text)
    {
        Node<Note> head = new Node<Note>(null,null);
        Node<Note> next = head;

        for(int i = 0; i < (text.length() / 2); i = i + 2)
        {
            // just example
            Note n = new Note(text.substring(i, i+2),1,440);
            next.setElement(n);
            next.setNext(new Node<Note>(null,null));
            next = next.getNext();
        }
        return head;
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
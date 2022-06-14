package com.example.betaversion;

import java.util.*;

/**
 * template of linked list
 *
 * @param <T> the type parameter
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  my implement to node T
 */
public class Node<T> {
    private T element;
    private Node<T> next;

    /**
     * Instantiates a new Node.
     *
     * @param e the element
     * @param n the next
     */
    public Node(T e, Node<T> n) {
        element = e;
        next = n;
    }

    /**
     * Cast from string to note node.
     *
     * @param text the text
     * @return the node
     */
    public static Node<Note> castFromStringToNote(String text)
    {
        String[] notes = text.split(" ");
        Node<Note> head = new Node<Note>(null,null);
        Node<Note> next = head;

        for(int i = 0; i < notes.length; i ++)
        {
            if (!notes[i].equals(""))
            {
                // just example
                if (!notes[i].equals("<SPACE>"))
                {
                    Note n = new Note(notes[i],1,Note.takeFreqency(notes[i])); // need to update
                    next.setElement(n);
                    next.setNext(new Node<Note>(null,null));
                    next = next.getNext();
                }

            }
        }
        return head;
    }

    /**
     * Gets element.
     *
     * @return the element
     */
    public T getElement() {
        return element;
    }

    /**
     * Sets element.
     *
     * @param e the element
     */
    public void setElement(T e) {
         this.element = e;
    }

    /**
     * convert it to arraylist .
     *
     * @return the array list
     */
    public ArrayList<T> toArraylist()
    {
        ArrayList<T> arr = new ArrayList<T>();

        Node<T> ptr = this;

        while (ptr != null)
        {
            if (ptr.getElement() != null)
            {
                arr.add(ptr.getElement());
            }
            ptr = ptr.getNext();
        }

        return arr;

    }


    /**
     * Gets next.
     *
     * @return the next
     */
    public Node<T> getNext() {
        return next;
    }

    /**
     * Sets next.
     *
     * @param n the next that we want to determine
     */
    public void setNext(Node<T> n) {
        next = n;
    }
}
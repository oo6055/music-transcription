package com.example.betaversion;
import java.util.ArrayList;


public class Section {
    private String uid;
    private Node<Note> composition;
    private boolean publicOrPrivate; // true - public
    private String nickName;
    private String date;
    private String nameOfFile;



    public Section(String uid, Node<Note> composition, String nickName, String date, boolean privateOrPublic, String nameOfFile) {

        this.uid = uid;
        this.composition = composition;
        this.nickName = nickName;
        this.date = date;
        this.publicOrPrivate = privateOrPublic;
        this.nameOfFile = nameOfFile;
    }


    public Section()
    {
    }

    public String getUid()
    {
        return uid;
    }
    public ArrayList<Note> getComposition()
    {
        return composition.toArraylist();
    }

    public Node<Note> NodeGetComposition()
    {
        return composition;
    }
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }
    public String getNickName()
    {
        return nickName;
    }

    public void setComposition(ArrayList<Note> n)
    {
        this.composition = new Node<Note>(n.get(0),null);
        Node<Note> ptr = composition;
        Node<Note> next = composition;

        for (int i = 1; i < n.size(); i++)
        {
            next = new Node<Note>(n.get(i),null);
            ptr.setNext(next);
            ptr = ptr.getNext();
        }
    }


    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public boolean getPublicOrPrivate() {
        return publicOrPrivate;
    }

    public void setPublicOrPrivate(boolean publicOrPrivate) {
        this.publicOrPrivate = publicOrPrivate;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    public void setNameOfFile(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }
}

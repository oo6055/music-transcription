package com.example.betaversion;
import com.example.betaversion.Note;
import com.example.betaversion.Node;
import java.util.ArrayList;
import java.util.List;


public class Section {
    private String uid;
    private Node<Note> composition;
    private boolean publicOrPrivate; // true - public
    private String nickName;
    private String date;


    public Section(String uid, Node<Note> composition,String nickName,String date, boolean privateOrPublic) {

        this.uid = uid;
        this.composition = composition;
        this.nickName = nickName;
        this.date = date;
        this.publicOrPrivate = privateOrPublic;
    }

    public ArrayList<Note> Composition()
    {
        return composition.toArraylist();
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
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public void setUid()
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
}

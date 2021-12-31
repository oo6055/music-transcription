package com.example.betaversion;
import com.example.betaversion.Note;
import com.example.betaversion.Node;




public class Section {
    private String uid;
    private Node<Note> composition;
    String nickName;
    String date;

    public Section(String uid, Node<Note> composition,String nickName,String date)
    {
        this.uid = uid;
        this.composition = composition;
        this.nickName = nickName;
        this.date = date;
    }
}

package com.example.betaversion;
import java.util.ArrayList;

/**
 * the section struct
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  represetnt a section
 */
public class Section {
    /**
     * The uid of the user.
     */
    private String uid;
    /**
     * The composition - the notes of the section.
     */
    private Node<Note> composition;
    private boolean publicOrPrivate; // true - public
    private String nickName;
    private String date;
    private String nameOfFile;


    /**
     * Instantiates a new Section.
     *
     * @param uid             the uid
     * @param composition     the composition
     * @param nickName        the nick name
     * @param date            the date
     * @param privateOrPublic the private or public
     * @param nameOfFile      the name of file
     */
    public Section(String uid, Node<Note> composition, String nickName, String date, boolean privateOrPublic, String nameOfFile) {

        this.uid = uid;
        this.composition = composition;
        this.nickName = nickName;
        this.date = date;
        this.publicOrPrivate = privateOrPublic;
        this.nameOfFile = nameOfFile;
    }


    /**
     * Instantiates a new Section.
     */
    public Section()
    {
    }

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public String getUid()
    {
        return uid;
    }

    /**
     * Gets composition.
     *
     * @return the composition
     */
    public ArrayList<Note> getComposition()
    {
        return composition.toArraylist();
    }

    /**
     * Node get composition node.
     *
     * @return the node
     */
    public Node<Note> NodeGetComposition()
    {
        return composition;
    }

    /**
     * Sets nick name.
     *
     * @param nickName the nick name
     */
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    public void setUid(String uid)
    {
        this.uid = uid;
    }

    /**
     * Gets nick name.
     *
     * @return the nick name
     */
    public String getNickName()
    {
        return nickName;
    }

    /**
     * Sets composition.
     *
     * @param n the n
     */
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


    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * Gets public or private.
     *
     * @return the public or private
     */
    public boolean getPublicOrPrivate() {
        return publicOrPrivate;
    }

    /**
     * Sets public or private.
     *
     * @param publicOrPrivate the public or private
     */
    public void setPublicOrPrivate(boolean publicOrPrivate) {
        this.publicOrPrivate = publicOrPrivate;
    }

    /**
     * Gets name of file.
     *
     * @return the name of file
     */
    public String getNameOfFile() {
        return nameOfFile;
    }

    /**
     * Sets name of file.
     *
     * @param nameOfFile the name of file
     */
    public void setNameOfFile(String nameOfFile) {
        this.nameOfFile = nameOfFile;
    }
}

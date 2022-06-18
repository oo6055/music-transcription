package com.example.betaversion;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * View the notes
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  view the notes
 */
public class MusicNotesView extends View {
    private ArrayList<String> notes; // not update many times
    private Bitmap structre;
    private ArrayList<MusicNoteCircle> middleOfCircles;
    /**
     * The radious
     */
    float r;
    /**
     * The height of the structre.
     */
    float height;

    /**
     * The dalteForNotes for note
     */
    private float dalteForNotes;
    /**
     * if we need to add more notes to the viewer
     */
    boolean notesAdded = false;
    /**
     * The Index of last touched.
     */
    int indexOfLastTouches;

    /**
     * Instantiates a new Music notes view.
     *
     * @param context the context
     */
    public MusicNotesView(Context context) {
        // if it gets a contex
        super(context);
        init(null);
    }

    /**
     * Instantiates a new Music notes view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public MusicNotesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Instantiates a new Music notes view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public MusicNotesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Instantiates a new Music notes view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     * @param defStyleRes  the def style res
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MusicNotesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * init the viewer and some fileds.
     *
     * @param set      the arrtibutes
     */
    private void init(@Nullable AttributeSet set)
    {
        // create the line with the clef
        structre = BitmapFactory.decodeResource(getResources(), R.drawable.musicnotesstructre);

        // when it gets the layout
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                structre = getResizesdBitMap(structre, getWidth(), getHeight());

            }
        });

        // init a new list
        notesAdded = true;
        middleOfCircles = new ArrayList<>();
        indexOfLastTouches = -1;

    }

    /**
     * Add notes.
     *
     * @param notes  the notes
     * @param width  the width
     * @param height the height
     */
    public void addNotes(ArrayList<String> notes, float width, float height)
    {
        int cxOfset = 0;
        r = height / 15;
        float posOfNote = 0;
        float offsetOfTheStart = width / 6;
        float horizontalOffset = width / 10;
        char special = 0;
        middleOfCircles = new ArrayList<>();


        for (int i = 0; i < notes.size(); i++)
        {
            special = 0;
            // get the position of the note (by his name)
            posOfNote = getPostion(height, notes.get(i));

            // check if the number is a sign
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '-')
            {
                special = '-';
            }
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '#')
            {
                special = '#';
            }

            middleOfCircles.add(new MusicNoteCircle(offsetOfTheStart + cxOfset ,posOfNote, special));
            cxOfset += horizontalOffset;
        }

    }

    /**
     * Add bamol to note.
     *
     * @param canvas  the canvas
     * @param xOfNote  the x coordinate of the bamol
     * @param yNote the y coordinate of the bamol
     * @param radius the radius of the note
     * @param delta the delta that it moves
     * @param fontSize the size of the font
     */
    private void addBamol(Canvas canvas, float xOfNote, float yNote, float radius, float delta,float fontSize) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextSize(fontSize);
        canvas.drawText("♭",xOfNote - delta - radius , yNote + p.getTextSize() / 14 ,p);
    }

    /**
     * Add diaz to note.
     *
     * @param canvas  the canvas
     * @param xOfNote  the x coordinate of the diaz
     * @param yNote the y coordinate of the diaz
     * @param radius the radius of the note
     * @param delta the delta that it moves
     * @param fontSize the size of the font
     */
    private void addDiaz(Canvas canvas, float xOfNote, float yNote, float radius, float delta,float fontSize) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextSize(fontSize);
        canvas.drawText("#",xOfNote - delta - radius, yNote + p.getTextSize() / 3,p);
    }

    /**
     * draw the circles by the center of them.
     *
     * @param canvas  the canvas
     * @param height  the height of the structre
     */
    private void drawCircles(Canvas canvas, int height)
    {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        float sizeOfLineVertical = height / 3;
        float width = canvas.getWidth();


        for (int i = 0; i < middleOfCircles.size(); i++)
        {
            // get the position of the note (by his name)

            // draw the note and the line
            canvas.drawCircle(middleOfCircles.get(i).getX() ,middleOfCircles.get(i).getY(),r ,p);
            canvas.drawLine(middleOfCircles.get(i).getX() + r ,middleOfCircles.get(i).getY(),middleOfCircles.get(i).getX() + r,middleOfCircles.get(i).getY() - sizeOfLineVertical ,p);
            // check if need to add more line in the bottom
            float notInTheMiddle = 1;

            // check if it is in a odd place
            float currentHigh = middleOfCircles.get(i).getY();

            // if the current height is devide in dalteForNotes
            if ((Math.round(currentHigh - (height - height / 10))) % Math.round(dalteForNotes * 2) == 0)
            {
                notInTheMiddle = 0;
            }

            // if the current height is less than first do
            while (Math.round(currentHigh) >= Math.round(height - height / 10.0))
            {
                float sizeOfLineHorizontal =  r + width / 70;
                canvas.drawLine(middleOfCircles.get(i).getX() - sizeOfLineHorizontal,currentHigh - notInTheMiddle * r,middleOfCircles.get(i).getX()   + sizeOfLineHorizontal, currentHigh - notInTheMiddle * r, p);
                currentHigh -= dalteForNotes * 2;
            }

            // deal with to high

            currentHigh = middleOfCircles.get(i).getY();
            notInTheMiddle = 1;
            // draw the lines
            if (((Math.round(currentHigh  - ((height - height / 10.0) - dalteForNotes * 12)) % Math.round(dalteForNotes * 2))) == 0)
            {
                float sizeOfLineHorizontal = width / 50;

                notInTheMiddle = 0;
                canvas.drawLine(middleOfCircles.get(i).getX() - sizeOfLineHorizontal,currentHigh + notInTheMiddle * dalteForNotes,middleOfCircles.get(i).getX()  + sizeOfLineHorizontal, currentHigh + notInTheMiddle * dalteForNotes, p);

            }

            // check if need to add more line in the high
            // check if need to add moreline in the high
            while (currentHigh <= height - height / 10 - dalteForNotes * 12 )
            {
                float sizeOfLineHorizontal = width / 50;
                canvas.drawLine(middleOfCircles.get(i).getX() - sizeOfLineHorizontal,currentHigh + notInTheMiddle * dalteForNotes,middleOfCircles.get(i).getX()  + sizeOfLineHorizontal, currentHigh + notInTheMiddle * dalteForNotes, p);
                currentHigh += dalteForNotes * 2;
            }

            // check if there is a sign
            if (middleOfCircles.get(i).getSpecial() == '-')
            {
                addBamol(canvas, middleOfCircles.get(i).getX(), middleOfCircles.get(i).getY(), r, width / 40, width / 20);
            }
            if (middleOfCircles.get(i).getSpecial() == '#')
            {
                addDiaz(canvas, middleOfCircles.get(i).getX(), middleOfCircles.get(i).getY(), r, width / 40, width / 24);
            }
        }
    }

    /**
     * get the y coordinate of note by his name.
     *
     * @param nameOfNote  the name of the note
     * @param height  the height of the structre
     */
    private float getPostion(float height, String nameOfNote) {
        char notes[] = {'c','d','e','f','g','a','b'};
        dalteForNotes = height / 18;
        float notePos =  height - height / 10 - findElement(notes, nameOfNote.charAt(0)) * dalteForNotes;
        int indexOfNumber = 0;

        // if the pos of the number is diffrent so I need to get him from diffrent index
        if (nameOfNote.length() >= 2 && (nameOfNote.charAt(1) == '-' || nameOfNote.charAt(1) == '#'))
        {
            indexOfNumber = 2;
        }
        else
        {
            indexOfNumber = 1;
        }
        // check if the second number is the octava of the note
        if (nameOfNote.length() >= indexOfNumber + 1 && nameOfNote.charAt(indexOfNumber) >= '0' && nameOfNote.charAt(indexOfNumber) <= '9')
        {
            notePos -= 7 * dalteForNotes * (int) ((nameOfNote.charAt(indexOfNumber) - '0') - 4);
        }

        return notePos;
    }

    /**
     * return index of note
     *
     * @param arr  the arr
     * @param toFind  the chat that we want to get his index
     * @return the index of the char
     */
    private int findElement(char[] arr, char toFind)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == toFind)
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * return index of note
     *
     * @param bitmap  the picture
     * @param width  desired width
     * @param height desired height
     * @return the new picture
     */
    private Bitmap getResizesdBitMap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();
        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0,0, width,height);

        matrix.setRectToRect(src,dst, Matrix.ScaleToFit.CENTER );
        return Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Gets section that the viewer shows
     *
     * @return the section
     */
    public Node<Note> getSection()
    {
        Node<Note> ptrOfNode = new Node<>(null,null);
        Node<Note> head = ptrOfNode;
        for (int i = 0; i < middleOfCircles.size(); i++)
        {
            String note = getNote(middleOfCircles.get(i).getY(), middleOfCircles.get(i).getSpecial());
            // need to change
            ptrOfNode.setElement(new Note(note, 1, Note.takeFreqency(note)));

            if (i != middleOfCircles.size() - 1)
            {
                ptrOfNode.setNext(new Node<>(null,null));
                ptrOfNode = ptrOfNode.getNext();
            }
        }

        return head;
    }

    /**
     * get a note from his y cords
     *
     * @param y  the y cords
     * @param special if there is a special sign near it
     * @return the name of the notes
     */
    private String getNote(float y, char special)
    {
        char notes[] = {'c','d','e','f','g','a','b'};
        // get the note pos (note hight - the first do) % num of notes in octave
        float theNotesPostions =  (-1 * (y - (height - height / 10)) + dalteForNotes * 7 * 3) % (dalteForNotes * 7);
        // get the do of the cotave
        float noteHight = (y + theNotesPostions);
        float octave = (noteHight - (height - height / 10)) / (dalteForNotes * 7);


        // check if the second number is the octava of the note
        float theNumberOfNotesOffset = (theNotesPostions / dalteForNotes);
        int index =  (int) Math.round(theNumberOfNotesOffset);

        int octaveOfNote = (Math.round(octave) * -1) + 4;

        if (index < 0)
        {
            index = notes.length + (index % 7);
        }
        if (special == 0)
        {
            String toReturn = String.valueOf(notes[index % 7]) + String.valueOf(octaveOfNote);
            return toReturn;
        }
        else
        {
            String valueOfSpecial = special == '#' ? "#" : "-";
            return String.valueOf(notes[index % 7]) + valueOfSpecial +  String.valueOf(octaveOfNote);
        }
    }

    /**
     * when it touched
     * @param event  the event
     * @return change the y cords for the note that touched
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                float x = event.getX();
                float y = event.getY();
                indexOfLastTouches = getIndex(x,y);

                // if there is a valid index
                if (indexOfLastTouches != -1)
                {
                    // if there is not accurate touch so make it accurate
                    if ((y - (height - height / 10)) % dalteForNotes < dalteForNotes / 2)
                    {
                        y -= (y - (height - height / 10)) % dalteForNotes;
                    }
                    else
                    {

                        y -= (y - (height - height / 10)) % dalteForNotes;

                    }

                    middleOfCircles.get(indexOfLastTouches).setY(y);
                }
                postInvalidate();
                return true;
            }
        }

        return value;
    }

    /**
     * get the index of the note that touched
     * @param x  x cords of the touch
     * @param y  y cords of the touch
     * @return the index of the note that touched
     */
    private int getIndex(float x, float y) {
        for (int i = 0; i < middleOfCircles.size(); i++)
        {
            float dalteX = (float) Math.pow(x - middleOfCircles.get(i).getX() ,2);
            float dalteY = (float) Math.pow(y - middleOfCircles.get(i).getY() ,2);

            if (dalteX + dalteY < Math.pow(r * 3.5 ,2))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * it init the viewer
     * @param canvas the canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(structre, 0,0,null);

        if (! notesAdded) {
            height = structre.getHeight();

            addNotes(notes, structre.getWidth(), structre.getHeight());
            notesAdded = true;


        }
        else
        {
            height = structre.getHeight();
            notes = convertToSectionOfStrings(getSection().toArraylist());
        }
        drawCircles(canvas, structre.getHeight());
    }

    /**
     * convert ArrayList<Note> to ArrayList<String> (with the name)
     * @param arr the arr that we want to convert
     * @return ArrayList<String> that it needs
     */
    private ArrayList<String> convertToSectionOfStrings(ArrayList<Note> arr) {
        ArrayList<String> notesArr = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++)
        {
            notesArr.add(arr.get(i).getName());
        }
        return notesArr;
    }

    /**
     * Sets notes.
     *
     * @param com the com
     */
    public void setNotes(Node<Note> com) {
        notes = new ArrayList<>();
        while (com != null)
        {
            if (com != null)
            {

                notes.add(com.getElement().getName().toLowerCase());
                com = com.getNext();
            }

        }
        addNotes(notes,structre.getWidth(), structre.getHeight());
        notesAdded = false;
        postInvalidate();
    }

    /**
     * Add note.
     *
     * @param note the note
     */
    public boolean addNote(String note) {
        float horizontalOffset = structre.getWidth() / 10;
        float offsetOfTheStart = structre.getWidth() / 6;


        if (middleOfCircles.size() == 0)
        {
            notes.add(note);
            addNotes(notes, structre.getWidth(), structre.getHeight());
            notesAdded = true;
            postInvalidate();

            return true;
        }

        // if it is get out of bounth
        if ((structre.getWidth() < offsetOfTheStart + horizontalOffset + middleOfCircles.get(middleOfCircles.size() - 1).getX()))
        {
            postInvalidate();
            return false;
        }
        else
        {
            notes.add(note);
            addNotes(notes, structre.getWidth(), structre.getHeight());
            notesAdded = true;
            postInvalidate();

            return true;
        }

    }

    /**
     * Remove note from the section
     */
    public void removeNote() {
        if (indexOfLastTouches != -1)
        {
            notes.remove(notes.get(indexOfLastTouches));
            addNotes(notes, structre.getWidth(), structre.getHeight());
            notesAdded = false;
            postInvalidate();
        }

    }

    /**
     * Add diaz.
     */
    public void addDiaz() {
        if (indexOfLastTouches != -1) {
            if (middleOfCircles.get(indexOfLastTouches).getSpecial() != '#')
            {
                middleOfCircles.get(indexOfLastTouches).setSpecial('#');
                postInvalidate();
            }
            else
            {
                middleOfCircles.get(indexOfLastTouches).setSpecial((char)0);
                postInvalidate();
            }

        }
    }

    /**
     * Add bamol.
     */
    public void addBamol() {
        if (indexOfLastTouches != -1)
        {
            if (middleOfCircles.get(indexOfLastTouches).getSpecial() != '-') {
                middleOfCircles.get(indexOfLastTouches).setSpecial('-');
                postInvalidate();
            }
            else
            {
                middleOfCircles.get(indexOfLastTouches).setSpecial((char)0);
                postInvalidate();
            }
        }
    }
}
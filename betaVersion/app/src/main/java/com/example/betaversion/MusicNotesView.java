package com.example.betaversion;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MusicNotesView extends View {
    private ArrayList<String> notes;
    private Bitmap structre;
    private float dalteForNotes;
    public MusicNotesView(Context context) {

        super(context);
        init(null);
    }

    public MusicNotesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MusicNotesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public MusicNotesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    private void init(@Nullable AttributeSet set)
    {

        structre = BitmapFactory.decodeResource(getResources(), R.drawable.musicnotesstructre);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                structre = getResizesdBitMap(structre, getWidth(), getHeight());
            }
        });

        notes = new ArrayList<>();
        notes.add("c4");
        notes.add("d4");
        notes.add("e4");
        notes.add("f4");
        notes.add("g5");
        notes.add("a4");
        notes.add("b4");
        notes.add("c4");
        notes.add("d4");


    }
    public void addNotes(ArrayList<String> notes, Canvas canvas, int height, int width)
    {
        int cxOfset = 0;
        float r = height / 18;
        float posOfNote = 0;
        float offsetOfTheStart = width / 6;
        Paint p = new Paint();
        float horizontalOffset = width / 15;
        p.setColor(Color.BLACK);
        for (int i = 0; i < notes.size(); i++)
        {
            // get the position of the note (by his name)
            posOfNote = getPostion(height, notes.get(i));

            // draw the note and the line
            canvas.drawCircle(offsetOfTheStart + cxOfset ,posOfNote,r ,p);
            canvas.drawLine(offsetOfTheStart + cxOfset + r ,posOfNote,offsetOfTheStart + cxOfset + r,posOfNote - height / 4 ,p);

            // check if need to add more line in the bottom
            float currentHigh = posOfNote;
            while (currentHigh >= height - height / 10)
            {
                int sizeOfLine = width / 50;
                canvas.drawLine(offsetOfTheStart+ cxOfset - sizeOfLine,currentHigh,offsetOfTheStart + cxOfset  + sizeOfLine, currentHigh, p);
                currentHigh -= dalteForNotes * 2;
            }

//            // check if need to add more line in the high
//            currentHigh = posOfNote;
//            while (currentHigh >= height - height / 10)
//            {
//                int sizeOfLine = width / 50;
//                canvas.drawLine(offsetOfTheStart+ cxOfset - sizeOfLine,currentHigh,offsetOfTheStart + cxOfset  + sizeOfLine, currentHigh, p);
//                currentHigh += dalteForNotes * 2;
//            }

            // check if the number is a sign
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '-')
            {
                addBamol(canvas, offsetOfTheStart + cxOfset, posOfNote, r, width / 40, width / 20);
            }
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '#')
            {
                addDiaz(canvas, offsetOfTheStart + cxOfset, posOfNote, r, width / 40, width / 24);
            }

            cxOfset += horizontalOffset;
        }

    }

    private void addBamol(Canvas canvas, float xOfNote, float yNote, float radius, float delta,float fontSize) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextSize(fontSize);
        canvas.drawText("♭",xOfNote - delta - radius , yNote + p.getTextSize() / 14 ,p);
    }

    private void addDiaz(Canvas canvas, float xOfNote, float yNote, float radius, float delta,float fontSize) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextSize(fontSize);
        canvas.drawText("#",xOfNote - delta - radius, yNote + p.getTextSize() / 3,p);
    }

    private float getPostion(int height, String s) {
        char notes[] = {'c','d','e','f','g','a','b'};
        dalteForNotes = height / 16;
        float notePos =  height - height / 10 - findElement(notes, s.charAt(0)) * dalteForNotes;

        // check if the second number is the octava of the note
        if (s.length() >= 2 && s.charAt(1) >= '0' && s.charAt(1) <= '9')
        {
            notePos -= 7 * dalteForNotes * (int) ((s.charAt(1) - '0') - 4);
        }
        return notePos;

    }
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

    private Bitmap getResizesdBitMap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();
        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0,0, width,height);

        matrix.setRectToRect(src,dst, Matrix.ScaleToFit.CENTER );
        return Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(structre, 0,0,null);

        addNotes(notes, canvas, structre.getHeight() , structre.getWidth());

    }
}

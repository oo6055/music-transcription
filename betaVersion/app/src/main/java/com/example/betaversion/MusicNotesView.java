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


public class MusicNotesView extends View {
    private ArrayList<String> notes;
    private Bitmap structre;
    private ArrayList<MusicNoteCircle> middleOfCircles;
    float r;
    private float dalteForNotes;
    boolean notesAdded = false;
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

    // need to remove
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        middleOfCircles = new ArrayList<>();
        notes = new ArrayList<>();
        notes.add("c4");
        notes.add("d4");
        notesAdded = false;



    }
    public void addNotes(ArrayList<String> notes, float width, float height)
    {
        int cxOfset = 0;
        r = height / 15;
        float posOfNote = 0;
        float offsetOfTheStart = width / 6;
        float horizontalOffset = width / 10;
        char special = 0;
        for (int i = 0; i < notes.size(); i++)
        {
            special = 0;
            // get the position of the note (by his name)
            posOfNote = getPostion(height, notes.get(i));

            // check if the number is a sign
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '-')
            {
                special = 'b';
            }
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '#')
            {
                special = 'd';
            }


            middleOfCircles.add(new MusicNoteCircle(offsetOfTheStart + cxOfset ,posOfNote, special));
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
            float notInTheMiddle = 0;

            // check if it is in a odd place
            float currentHigh = middleOfCircles.get(i).getY();
            if ((((int)currentHigh  - (int)((float)height - (float)height / 10.0)) % (dalteForNotes * 2)) != 0)
            {
                notInTheMiddle = 1;
            }

            while ((int)currentHigh >= (int)((float)height - (float)height / 10.0))
            {
                float sizeOfLineHorizontal = width / 50;
                canvas.drawLine(middleOfCircles.get(i).getX() - sizeOfLineHorizontal,currentHigh - notInTheMiddle * dalteForNotes,middleOfCircles.get(i).getX()   + sizeOfLineHorizontal, currentHigh - notInTheMiddle * dalteForNotes, p);
                currentHigh -= dalteForNotes * 2;
            }



            currentHigh = middleOfCircles.get(i).getY();
            if (((currentHigh  - (height - height / 10 - dalteForNotes * 12 )) % (dalteForNotes * 2)) != 0)
            {
                notInTheMiddle = 1;
            }

            // check if need to add more line in the high
            while (currentHigh <= height - height / 10 - dalteForNotes * 12 )
            {
                float sizeOfLineHorizontal = width / 50;
                canvas.drawLine(middleOfCircles.get(i).getX() - sizeOfLineHorizontal,currentHigh + notInTheMiddle * dalteForNotes,middleOfCircles.get(i).getX()  + sizeOfLineHorizontal, currentHigh + notInTheMiddle * dalteForNotes, p);
                currentHigh += dalteForNotes * 2;
            }

            // check if the number is a sign
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '-')
            {
                addBamol(canvas, middleOfCircles.get(i).getX(), middleOfCircles.get(i).getY(), r, width / 40, width / 20);
            }
            if (notes.get(i).length() >= 2 && notes.get(i).charAt(1) == '#')
            {
                addDiaz(canvas, middleOfCircles.get(i).getX(), middleOfCircles.get(i).getY(), r, width / 40, width / 24);
            }
        }
    }


    private float getPostion(float height, String s) {
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
                int index = getIndex(x,y);

                if (index != -1)
                {
                    if (y % dalteForNotes < dalteForNotes / 2)
                    {
                        y -= y % dalteForNotes;
                    }
                    middleOfCircles.get(index).setY(y);
                }
                postInvalidate();
                return true;
            }
        }

        return value;
    }

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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(structre, 0,0,null);

        if (! notesAdded)
        {
            addNotes(notes, structre.getWidth(), structre.getHeight());
            notesAdded = true;
        }


        drawCircles(canvas, structre.getHeight());
    }
}

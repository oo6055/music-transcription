package com.example.betaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * this activity is showing all the valid sections
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  this activity is showing all the valid sections
 */
public class ShowAllValidSections extends AppCompatActivity {

    /**
     * The list view
     */
    ListView ls;
    /**
     * The Sections list.
     */
    ArrayList<Section> sectionsList;
    /**
     * The navigation btn.
     */
    BottomNavigationView btnnav;

    /**
     * For PDF.
     */
    float dalteForNotes;
    float r;
    ArrayList<MusicNoteCircle> middleOfCircles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_valid_sections);

        sectionsList = new ArrayList<Section>();
        btnnav = (BottomNavigationView) findViewById(R.id.btnnav);
        btnnav.setSelectedItemId(R.id.publicsec);

        btnnav.setOnNavigationItemSelectedListener(bottomNavMethod);


        ls = (ListView) findViewById(R.id.ls);
        getAllPublicSections();
        ls.setOnCreateContextMenuListener(this);
    }


    /**
     * get the the public sections
     *
     */
    private void getAllPublicSections() {


        FBref.FBDB.getReference().child("Public Sections")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        sectionsList = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {

                            for (DataSnapshot sc : ds.getChildren())
                            {
                                Section s = sc.getValue(Section.class);
                                sectionsList.add(s);
                            }

                        }

                        updateListView(sectionsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (databaseError != null) {
                        }
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getTitle().equals("show my sections")) {
                        Intent si = new Intent(ShowAllValidSections.this, ShowMySections.class);
                        startActivity(si);
                        finish();
                    }
                    return false;
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        getAllPublicSections();
    }

    /**
     * onCreateContextMenu
     * Short description.
     * onCreateContextMenu listener use for the ContextMenu
     * <p>
     *     ContextMenu menu
     *     View v
     *     ContextMenu.ContextMenuInfo menuInfo
     *
     * @param  menu - the object,v - the item that selected ,menuInfo - the info
     * @return	none
     */
    //@Overrid
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("options");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sectionsoptions, menu);
    }

    /**
     * onContextItemSelected
     * Short description.
     * onContextItemSelected listener use for the ContextMenu
     * <p>
     *     MenuItem item
     *
     * @param  item - the item that selected
     * @return	true if it worked
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String op = item.getTitle().toString();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int i = info.position;
        Intent si;


        if (op.equals("play section"))
        {
            si = new Intent(this,PlaySection.class);
            si.putExtra("fileName",sectionsList.get(i).getNameOfFile());
            si.putExtra("nickname",sectionsList.get(i).getNickName());

            startActivity(si);

        }
        else if (op.equals("get transcript"))
        {
            if (sectionsList.get(i).getComposition() != null)
            {
                File pdf = createPdf(convertToSectionOfStrings(sectionsList.get(i).getComposition()), sectionsList.get(i).getNickName());
                openPDF(pdf);
            }
            else
            {
                Toast.makeText(ShowAllValidSections.this, "not valid section", Toast.LENGTH_SHORT).show();
            }
        }
        else if (op.equals("change section"))
        {
            si = new Intent(this,ChangeNotes.class);
            si.putExtra("fileName",sectionsList.get(i).getNameOfFile());
            si.putExtra("uid",sectionsList.get(i).getUid());
            si.putExtra("privacy",sectionsList.get(i).getPublicOrPrivate());


            startActivity(si);
        }
        else if (op.equals("delete section"))
        {
            if (sectionsList.get(i).getUid() != FBref.mAuth.getUid())
            {
                Toast.makeText(ShowAllValidSections.this, "you don't have permission for it", Toast.LENGTH_SHORT).show();
            }
            else
            {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query deleteQuery = ref.child(sectionsList.get(i).getPublicOrPrivate() ? "Public Sections" : "Private Sections").child(sectionsList.get(i).getUid()).orderByChild("date").equalTo(sectionsList.get(i).getDate());

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            Toast.makeText(ShowAllValidSections.this, "deleted", Toast.LENGTH_SHORT).show();
                        }
                        getAllPublicSections();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            sectionsList = new ArrayList<>();
            getAllPublicSections();
        }

        return true;
    }

    // Access pdf from storage and using to Intent get options to view application in available applications.
    private void openPDF(File file) {


        // Get the URI Path of file.
        Uri uriPdfPath = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                BuildConfig.APPLICATION_ID + ".provider", file);

        // Start Intent to View PDF from the Installed Applications.
        Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
        pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenIntent.setClipData(ClipData.newRawUri("", uriPdfPath));
        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf");
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |  Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            startActivity(pdfOpenIntent);
        } catch (ActivityNotFoundException activityNotFoundException) {
            Toast.makeText(this,"There is no app to load corresponding PDF",Toast.LENGTH_LONG).show();

        }
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

    public File createPdf(ArrayList<String> notes, String nickName)
    {
        PdfDocument pdf = new PdfDocument();
        PdfDocument.PageInfo pdfInfo = new PdfDocument.PageInfo.Builder(1500, 2010, 1).create();
        PdfDocument.Page page = pdf.startPage(pdfInfo);
        Canvas canvas = page.getCanvas();

        // create the line with the clef


        Bitmap structre = BitmapFactory.decodeResource(getResources(), R.drawable.musicnotesstructre);
        structre = getResizesdBitMap(structre, canvas.getWidth(), canvas.getHeight());

        float counter = 0;
        while (counter < canvas.getHeight())
        {
            canvas.drawBitmap(structre, 0,counter,null);
            counter += structre.getHeight() + 3;

        }
        canvas.drawBitmap(structre, 0,0,null);


        addNotes(notes, structre.getWidth(), structre.getHeight());
        drawCircles(canvas, structre.getHeight());




        pdf.finishPage(page);
        File mypath=new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),nickName + ".pdf");

        try {
            pdf.writeTo(new FileOutputStream(mypath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdf.close();
        return mypath;
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
        float factor = 0;

        for (int i = 0; i < middleOfCircles.size(); i++)
        {
            // get the position of the note (by his name)
            float XOfNote = middleOfCircles.get(i).getX();
            float YOfNote = middleOfCircles.get(i).getY();
            factor = 0;

            while(XOfNote > canvas.getWidth())
            {
                YOfNote += height;
                XOfNote -= canvas.getWidth();
                factor += height;
            }

            middleOfCircles.get(i).setY(YOfNote);
            middleOfCircles.get(i).setX(XOfNote);

            // draw the note and the line
            canvas.drawCircle(middleOfCircles.get(i).getX() ,middleOfCircles.get(i).getY(),r ,p);
            canvas.drawLine(middleOfCircles.get(i).getX() + r ,middleOfCircles.get(i).getY(),middleOfCircles.get(i).getX() + r,middleOfCircles.get(i).getY() - sizeOfLineVertical ,p);
            // check if need to add more line in the bottom
            float notInTheMiddle = 1;

            // check if it is in a odd place
            float currentHigh = middleOfCircles.get(i).getY();

            // if the current height is devide in dalteForNotes
            if ((Math.round(currentHigh - (factor + height - height / 10))) % Math.round(dalteForNotes * 2) == 0)
            {
                notInTheMiddle = 0;
            }

            // if the current height is less than first do
            while (Math.round(currentHigh) >= Math.round(factor + height - height / 10.0))
            {
                float sizeOfLineHorizontal =  r + width / 70;
                canvas.drawLine(middleOfCircles.get(i).getX() - sizeOfLineHorizontal,currentHigh - notInTheMiddle * r,middleOfCircles.get(i).getX()   + sizeOfLineHorizontal, currentHigh - notInTheMiddle * r, p);
                currentHigh -= dalteForNotes * 2;
            }

            // deal with to high

            currentHigh = middleOfCircles.get(i).getY();
            notInTheMiddle = 1;
            // draw the lines
            if (((Math.round(currentHigh  - ((factor + height - height / 10.0) - dalteForNotes * 12)) % Math.round(dalteForNotes * 2))) == 0)
            {
                float sizeOfLineHorizontal = width / 50;

                notInTheMiddle = 0;
                canvas.drawLine(middleOfCircles.get(i).getX() - sizeOfLineHorizontal,currentHigh + notInTheMiddle * dalteForNotes,middleOfCircles.get(i).getX()  + sizeOfLineHorizontal, currentHigh + notInTheMiddle * dalteForNotes, p);

            }

            // check if need to add more line in the high
            // check if need to add moreline in the high
            while (currentHigh <= factor + height - height / 10 - dalteForNotes * 12 )
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
        float horizontalOffset = width / 14;
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
            if (((offsetOfTheStart + cxOfset) % width) < width / 8)
            {
                cxOfset = (int) ((width / 6) + (cxOfset - (width / 6)  / width));
            }
        }

    }
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
     * get the the public sections
     *
     * @param  sectionsList the list of sections that we want to show
     */
    private void updateListView(ArrayList<Section> sectionsList) {

        MyListAdapter adapter=new MyListAdapter(this,sectionsList);
        ls.setAdapter(adapter);
    }

    /**
     * onCreateContextMenu
     * Short description.
     * onCreateContextMenu listener use for the ContextMenu
     * <p>
     *     ContextMenu menu
     *     View v
     *     ContextMenu.ContextMenuInfo menuInfo
     *
     * @param  menu - the object,v - the item that selected ,menuInfo - the info
     * @return	true if it success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.generalmenu, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * Short description.
     * what happen if an item was selected
     * <p>
     *     MenuItem item
     *
     * @param  item - the menuItem
     * @return	true if it success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String whatClicked = (String) item.getTitle();
        Intent si;

        if(whatClicked.equals("show my sections"))
        {
            si = new Intent(this,ShowMySections.class);
            startActivity(si);
        }
        else if(whatClicked.equals("signout"))
        {
            FBref.mAuth.signOut();
            si = new Intent(this, SignInActivity.class);
            startActivity(si);
        }

        return  true;
    }
}
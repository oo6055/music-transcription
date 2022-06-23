 package com.example.betaversion;

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
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

 /**
  * this activity is showing my sections
  *
  * @author Ori Ofek <oriofek106@gmail.com>
  * @version 1
  * @since 21 /4/2021  this activity is showing all the valid sections
  */
 public class ShowMySections extends AppCompatActivity  implements View.OnCreateContextMenuListener {
     /**
      * The list view
      */
     ListView ls;
     /**
      * The Fab.
      */
     FloatingActionButton fab;

     /**
      * For PDF.
      */
     float dalteForNotes;
     float r;
     ArrayList<MusicNoteCircle> middleOfCircles;

     /**
      * if the fab is open
      */
     boolean isFABOpen;
     /**
      * if the fab is open
      */

     /**
      * The add fab.
      */
     FloatingActionButton addfab;
     /**
      * The Sections list.
      */
     ArrayList<Section> allSectionsList;

     ArrayList<Section> publicSectionList;
     ArrayList<Section> privateSectionList;
     /**
      * The navigation bottom.
      */
     BottomNavigationView btnnav;
     /**
      * The Hello text view.
      */
     TextView hello;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_show_my_sections);

         isFABOpen = false;
         btnnav = (BottomNavigationView) findViewById(R.id.btnnav);
         hello = (TextView) findViewById(R.id.title);
         fab = (FloatingActionButton) findViewById(R.id.fb);
         addfab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
         fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 setAnimation();
                 setVisible();
                 if (!isFABOpen) {
                     showFABMenu();
                 } else {
                     closeFABMenu();
                 }
                 isFABOpen = !isFABOpen;
             }
         });

         btnnav.setOnNavigationItemSelectedListener(bottomNavMethod);

         addfab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent si = new Intent(ShowMySections.this, GetSection.class);
                 startActivity(si);
             }
         });


         ls = (ListView) findViewById(R.id.ls);
         getUserSections();
         ls.setOnCreateContextMenuListener(this);
     }

     @Override
     protected void onResume() {
         super.onResume();
         getUserSections();
     }

     private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
             BottomNavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                     if (item.getTitle().equals("search sections")) {
                         Intent si = new Intent(ShowMySections.this, ShowAllValidSections.class);
                         startActivity(si);
                         finish();
                     }
                     return false;
                 }
     };

     /**
      * show the add fab.
      */
    private void showFABMenu(){
        addfab.startAnimation(AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim));
    }
     /**
      * close the fab.
      */
    private void closeFABMenu(){
        addfab.animate().translationY(0);
    }

     /**
      * Sets animation.
      */
     public void setAnimation()
    {
        if (!isFABOpen)
        {
            addfab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_btn_anim));
            fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim));
        }
        else
        {
            addfab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_btn_anim));
            fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim));
        }
    }


     /**
      * creates pdf file.
      */
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
      * Sets visible.
      */
     public void setVisible()
    {
        if (!isFABOpen)
        {
            addfab.setVisibility(View.VISIBLE);
        }
        else
        {
            addfab.setVisibility(View.INVISIBLE);
        }
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
         allSectionsList = (ArrayList<Section>) privateSectionList.clone();
         allSectionsList.addAll(publicSectionList);


         if (op.equals("play section"))
         {
             si = new Intent(this,PlaySection.class);
             si.putExtra("fileName",allSectionsList.get(i).getNameOfFile());
             si.putExtra("nickname",allSectionsList.get(i).getNickName());

             startActivity(si);

         }
         else if (op.equals("get transcript"))
         {
             if (allSectionsList.get(i).getComposition() != null)
             {
                 File pdf = createPdf(convertToSectionOfStrings(allSectionsList.get(i).getComposition()), allSectionsList.get(i).getNickName());
                 openPDF(pdf);
             }
             else
             {
                 Toast.makeText(ShowMySections.this, "not valid section", Toast.LENGTH_SHORT).show();
             }

         }
         else if (op.equals("change section"))
         {
             si = new Intent(this,ChangeNotes.class);
             si.putExtra("fileName",allSectionsList.get(i).getNameOfFile());
             si.putExtra("privacy",allSectionsList.get(i).getPublicOrPrivate());
             si.putExtra("uid",allSectionsList.get(i).getUid());



             startActivity(si);
         }
         else if (op.equals("delete section"))
         {
             DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
             Query deleteQuery = ref.child(allSectionsList.get(i).getPublicOrPrivate() ? "Public Sections" : "Private Sections").child(allSectionsList.get(i).getUid()).orderByChild("date").equalTo(allSectionsList.get(i).getDate());

             deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                         appleSnapshot.getRef().removeValue();
                     }
                     getUserSections();
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {
                 }
             });


             getUserSections();
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

     /**
      * this func update the list view of the sections
      *
      */
    private void getUserSections() {

        // get the name of the user
        FBref.FBDB.getReference().child("Users").child(FBref.mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                }
                else {
                    hello.setText("Hello " + String.valueOf(task.getResult().getValue()));
                }
            }
        });


        FBref.FBDB.getReference().child("Private Sections").child(FBref.mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        privateSectionList = new ArrayList<>();

                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Section s = ds.getValue(Section.class);
                            privateSectionList.add(s);
                        }

                        getPublicSections();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

     /**
      * this func gets the public sections
      *
      */
    private void getPublicSections() {


        FBref.FBDB.getReference().child("Public Sections").child(FBref.mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        publicSectionList = new ArrayList<Section>();

                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Section s = ds.getValue(Section.class);
                            publicSectionList.add(s);
                        }
                        allSectionsList = (ArrayList<Section>) privateSectionList.clone();
                        allSectionsList.addAll(publicSectionList);
                        updateListView(allSectionsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (databaseError != null) {
                        }
                    }
                });
    }

     /**
      * this function update the listview
      *
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


         if(whatClicked.equals("signout"))
         {
             FBref.mAuth.signOut();
             si = new Intent(this, SignInActivity.class);
             startActivity(si);
         }
         else if(whatClicked.equals("credits"))
         {
             FBref.mAuth.signOut();
             si = new Intent(this, Credits.class);
             startActivity(si);

         }

         return  true;
     }
}
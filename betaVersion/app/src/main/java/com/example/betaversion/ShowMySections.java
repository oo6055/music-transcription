 package com.example.betaversion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


 public class ShowMySections extends AppCompatActivity  implements View.OnCreateContextMenuListener {
    ListView ls;
    FloatingActionButton fab;
    boolean isFABOpen;
    FloatingActionButton addfab;
    ArrayList<Section> sectionsList;
    TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_sections);


        isFABOpen = false;
        hello = (TextView) findViewById(R.id.title);
        fab = (FloatingActionButton) findViewById(R.id.fb);
        addfab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimation();
                setVisible();
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
                isFABOpen=!isFABOpen;
            }
        });

        sectionsList = new ArrayList<Section>();

        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent si = new Intent(ShowMySections.this , GetSection.class);
                startActivity(si);
            }
            });


        ls = (ListView) findViewById(R.id.ls);
        getUserSections();
        ls.setOnCreateContextMenuListener(this);
    }

    private void showFABMenu(){
        addfab.startAnimation(AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim));
    }

    private void closeFABMenu(){
        addfab.animate().translationY(0);
    }

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


         if (op.equals("play section"))
         {
             si = new Intent(this,PlaySection.class);
             si.putExtra("fileName",sectionsList.get(i).getNameOfFile());
             si.putExtra("nickname",sectionsList.get(i).getNickName());

             startActivity(si);

         }
         else if (op.equals("get transcript"))
         {
             String pathInFireBase = sectionsList.get(i).getNameOfFile();
             pathInFireBase = pathInFireBase.substring(0,pathInFireBase.indexOf(".")) + ".pdf";
             StorageReference pdfRef = FBref.filesRef.child("/" + pathInFireBase);
             pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri downloadUrl) {
                     Intent intent = new Intent(Intent.ACTION_VIEW);
                     intent.setDataAndType(downloadUrl, "application/pdf");

                     // FLAG_GRANT_READ_URI_PERMISSION is needed on API 24+ so the activity opening the file can read it
                     intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                     if (intent.resolveActivity(getPackageManager()) == null) {
                         // Show an error
                     } else {
                         startActivity(intent);
                     }
                 }
             });
         }
         else if (op.equals("change section"))
         {
             si = new Intent(this,ChangeNotes.class);
             si.putExtra("fileName",sectionsList.get(i).getNameOfFile());
             si.putExtra("privacy",sectionsList.get(i).getPublicOrPrivate());


             startActivity(si);
         }

         return true;
     }

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


                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Section s = ds.getValue(Section.class);
                            sectionsList.add(s);
                        }

                        getPublicSections();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        // get th
    }

    private void getPublicSections() {


        FBref.FBDB.getReference().child("Public Sections").child(FBref.mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Section s = ds.getValue(Section.class);
                            sectionsList.add(s);
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

         if(whatClicked.equals("search sections"))
         {
             si = new Intent(this,ShowAllValidSections.class);
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
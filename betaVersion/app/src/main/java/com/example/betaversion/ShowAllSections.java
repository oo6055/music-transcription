 package com.example.betaversion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import com.google.firebase.database.ValueEventListener;


 public class ShowAllSections extends AppCompatActivity {
    ListView ls;
    FloatingActionButton fab;
    boolean isFABOpen;
    FloatingActionButton addfab;
    ArrayList<Section> sectionsList;
    TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_section);


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
                Intent si = new Intent(ShowAllSections.this , GetSection.class);
                startActivity(si);
            }
            });


        ls = (ListView) findViewById(R.id.ls);
        getUserSections();
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
}
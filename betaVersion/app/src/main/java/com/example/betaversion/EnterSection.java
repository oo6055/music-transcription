package com.example.betaversion;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

public class EnterSection extends AppCompatActivity {
    ListView ls;
    FloatingActionButton fab;
    boolean isFABOpen;
    FloatingActionButton addfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_section);


        isFABOpen = false;
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

        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(EnterSection.this, "click", Toast.LENGTH_SHORT).show();
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
        String uid = FBref.mAuth.getUid();

        DatabaseReference privateSectionCase = FBref.FBDB.getReference().child("Private Sections");
        privateSectionCase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                ArrayList<Section> sectionsList = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Section sec = ds.getValue(Section.class);
                    sectionsList.add(sec);
                }

                updateListView(sectionsList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void updateListView(ArrayList<Section> sectionsList) {

        MyListAdapter adapter=new MyListAdapter(this,sectionsList);
        ls.setAdapter(adapter);

    }
}
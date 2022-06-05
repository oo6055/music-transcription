package com.example.betaversion;

import static com.nitishp.sheetmusic.NoteData.NoteDuration.FOURTH;
import static com.nitishp.sheetmusic.NoteData.NoteValue.LOWER_B;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nitishp.sheetmusic.MusicBarView;
import com.nitishp.sheetmusic.NoteData;

import java.util.ArrayList;

public class ChangeNotes extends AppCompatActivity {
    Section curr;
    MusicNotesView musicNotesView;
    String privacy;
    String uid;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_notes);

        musicNotesView = (MusicNotesView) findViewById(R.id.musicNotesView);



        Intent gi = getIntent();
        String name = gi.getStringExtra("fileName");
        boolean typeOfPrivacy = gi.getBooleanExtra("privacy", true);
        uid = gi.getStringExtra("uid");
        privacy = typeOfPrivacy ? "Public Sections" : "Private Sections";


        Query q = FBref.FBDB.getReference().child(privacy).child(uid).orderByChild("nameOfFile").equalTo(name);
        q.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curr = dataSnapshot.getValue(Section.class);
                // get the path of the section (for the update)
                address = dataSnapshot.getKey();
                for (DataSnapshot sec : dataSnapshot.getChildren())
                {
                    curr = sec.getValue(Section.class);
                }
                Node<Note> com = curr.NodeGetComposition();

                musicNotesView.setNotes(com);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                }
            }
        });
    }

    public void saveNotes(View view) {
        Node<Note> theNotes = musicNotesView.getSection();
        curr.setComposition(theNotes.toArraylist());

        String notes = "";

        while (theNotes != null)
        {
            notes += theNotes.getElement().name + " ";
            theNotes = theNotes.getNext();
        }

        if (uid != FBref.mAuth.getUid())
        {
            new AlertDialog.Builder(ChangeNotes.this)
                    .setTitle("Public Or Private")
                    .setMessage("It is not your section! do you want public or private acesses?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(
                    "public",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            FBref.FBDB.getReference().child("Private Sections").child(FBref.mAuth.getUid()).push().setValue(curr);
                        }
                    })

                    .setNegativeButton(
                    "Private",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            FBref.FBDB.getReference().child("Private Sections").child(FBref.mAuth.getUid()).push().setValue(curr);
                        }
                    })
                    .show();

        }
        else // if the user created the section
        {
            FBref.FBDB.getReference().child(privacy).child(uid).child(address).setValue(curr);
        }


        Toast.makeText(this, notes, Toast.LENGTH_SHORT).show();
    }

    public void addNote(View view) {
        musicNotesView.addNote("c4");

    }

    public void addBamol(View view) {
        musicNotesView.addBamol();
    }

    public void addDiaz(View view) {
        musicNotesView.addDiaz();
    }
}
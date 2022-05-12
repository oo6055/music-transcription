package com.example.betaversion;

import static com.nitishp.sheetmusic.NoteData.NoteDuration.FOURTH;
import static com.nitishp.sheetmusic.NoteData.NoteValue.LOWER_B;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_notes);


        Intent gi = getIntent();
        String name = gi.getStringExtra("fileName");
        boolean typeOfPrivacy = gi.getBooleanExtra("privacy", true);
        String privacy = typeOfPrivacy ? "Public Sections" : "Private Sections";
        Query q = FBref.FBDB.getReference().child(privacy).child(FBref.mAuth.getUid()).orderByChild("nameOfFile").equalTo(name);
        q.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curr = dataSnapshot.getValue(Section.class);
                Node<Note> com = curr.NodeGetComposition();
                String sec = "";
                while (com != null)
                {
                    sec += com.getElement().name + " ";
                    com = com.getNext();
                }
                Toast.makeText(ChangeNotes.this, sec, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                }
            }
        });
    }
}
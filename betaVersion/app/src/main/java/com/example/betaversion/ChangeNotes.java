package com.example.betaversion;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ChangeNotes extends AppCompatActivity {
    Section curr;
    MusicNotesView musicNotesView;
    String privacy;
    String uid;
    String address;

    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private final int sampleRate = 8000;
    private int numSamples = 0;
    private ArrayList<Double> sample;// hz

    ArrayList<Byte>  generatedSnd;

    Handler handler = new Handler();


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
            notes += theNotes.getElement().getName() + " ";
            theNotes = theNotes.getNext();
        }

        if (!uid.equals(FBref.mAuth.getUid()))
        {
            new AlertDialog.Builder(ChangeNotes.this)
                    .setTitle("Public Or Private")
                    .setMessage("It is not your section! do you want public or private acesses?")


                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(
                    "Public",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            curr.setUid(FBref.mAuth.getUid());
                            curr.setPublicOrPrivate(true);
                            FBref.FBDB.getReference().child("Public Sections").child(FBref.mAuth.getUid()).push().setValue(curr);
                        }
                    })

                    .setNegativeButton(
                    "Private",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            curr.setUid(FBref.mAuth.getUid());
                            curr.setPublicOrPrivate(false);
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

    public void playSection(View view) {

        // Use a new tread as this can take a while
        final Thread thread = new Thread(new Runnable() {
            public void run() {



                    handler.post(new Runnable() {

                        public void run() {

                            genTone(curr.getComposition());
                            playSound();
                        }
                    });

            }
        });
        thread.start();




    }

    void genTone(ArrayList<Note> notes){
        // fill out the array

        sample = new ArrayList<>();
        generatedSnd = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++)
        {
            // calculate the num of samples
            numSamples = (int) (notes.get(i).getDuration() * sampleRate);


            for (int j = 0; j < numSamples; j++) {
                sample.add(Math.sin(2 * Math.PI * j / (sampleRate/notes.get(i).getFreqency())));
            }

            // put a lit of silence
            numSamples = (int) (0.1 * sampleRate);

            for (int j = 0; j < numSamples; j++) {
                sample.add(0.0);
            }

        }





        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        for (int i = 0; i < sample.size(); i++) {

            double dVal = sample.get(i);
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd.add((byte) (val & 0x00ff));
            generatedSnd.add((byte) ((val & 0xff00) >>> 8));

        }
    }

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.size(),
                AudioTrack.MODE_STATIC);
        audioTrack.write(convertListToByte(generatedSnd), 0, generatedSnd.size());
        audioTrack.play();
    }
    byte[] convertListToByte(ArrayList<Byte> generatedSnd)
    {
        byte[] arr = new byte[generatedSnd.size()];

        for (int i = 0; i < generatedSnd.size(); i++)
        {
            arr[i] = generatedSnd.get(i);
        }
        return  arr;
    }
}
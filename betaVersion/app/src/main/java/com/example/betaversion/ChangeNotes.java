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
import java.util.Date;

/**
 * The Change notes activity.
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021
 *  the activity that changes the notes
 */
public class ChangeNotes extends AppCompatActivity {
    /**
     * the section that is running
     */
    Section curr;
    /**
     * the viewer that I wrote
     */
    MusicNotesView musicNotesView;
    /**
     * what is the privacy
     */
    String privacy;
    /**
     * The uid of the chosen section
     */
    String uid;
    /**
     * The address of the secion
     */
    String address;

    private final int sampleRate = 8000;
    private int numSamples = 0;
    private ArrayList<Double> sample;// hz

    /**
     * The sound that it will make.
     */
    ArrayList<Byte>  generatedSnd;

    /**
     * The Handler which play the sound
     */
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_notes);

        musicNotesView = (MusicNotesView) findViewById(R.id.musicNotesView);

        // get data from intent (abount the section)
        Intent gi = getIntent();
        String name = gi.getStringExtra("fileName");
        boolean typeOfPrivacy = gi.getBooleanExtra("privacy", true);
        uid = gi.getStringExtra("uid");
        privacy = typeOfPrivacy ? "Public Sections" : "Private Sections";

        // get a query to get the section
        Query q = FBref.FBDB.getReference().child(privacy).child(uid).orderByChild("nameOfFile").equalTo(name);
        q.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // get the path of the section (for the update)
                address = dataSnapshot.getKey();
                for (DataSnapshot sec : dataSnapshot.getChildren())
                {
                    curr = sec.getValue(Section.class);
                }

                // set the viewer
                musicNotesView.setNotes(curr.NodeGetComposition());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                }
            }
        });
    }

    /**
     * Save notes.
     *
     * @param view the view
     */
    public void saveNotes(View view) {
        // set the composition
        Node<Note> theNotes = musicNotesView.getSection();
        curr.setComposition(theNotes.toArraylist());

        String notes = "";

        // get the section to string
        while (theNotes != null)
        {
            notes += theNotes.getElement().getName() + " ";
            theNotes = theNotes.getNext();
        }
        // if the section is not belong to user He can't touch it
        if (!curr.getUid().equals(FBref.mAuth.getUid()))
        {
            new AlertDialog.Builder(ChangeNotes.this)
                    .setTitle("Public Or Private")
                    .setMessage("It is not your section! do you want public or private acesses?")


                    .setPositiveButton(
                    "Public",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                            curr.setUid(FBref.mAuth.getUid());
                            curr.setPublicOrPrivate(true);
                            curr.setDate((new Date()).toString());
                            FBref.FBDB.getReference().child("Public Sections").child(FBref.mAuth.getUid()).push().setValue(curr);
                        }
                    })

                    .setNegativeButton(
                    "Private",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            curr.setUid(FBref.mAuth.getUid());
                            curr.setDate((new Date()).toString());
                            curr.setPublicOrPrivate(false);
                            FBref.FBDB.getReference().child("Private Sections").child(FBref.mAuth.getUid()).push().setValue(curr);
                        }
                    })
                    .show();

        }
        else // if the user created the section
        {
            curr.setDate((new Date()).toString());
            FBref.FBDB.getReference().child(privacy).child(uid).child(address).setValue(curr);
        }


        Toast.makeText(this, notes, Toast.LENGTH_SHORT).show();
    }

    /**
     * Add note.
     *
     * @param view the view
     */
    public void addNote(View view) {
        musicNotesView.addNote("c4");

    }

    /**
     * Add bamol.
     *
     * @param view the view
     */
    public void addBamol(View view) {
        musicNotesView.addBamol();
    }

    /**
     * Add diaz.
     *
     * @param view the view
     */
    public void addDiaz(View view) {
        musicNotesView.addDiaz();
    }

    /**
     * Play section.
     *
     * @param view the view
     */
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

    /**
     * Gen tone.
     *
     * @param notes - the notes that we wanna add
     */
    void genTone(ArrayList<Note> notes){
        sample = new ArrayList<>();
        generatedSnd = new ArrayList<>();

        for (int i = 0; i < notes.size(); i++)
        {
            // calculate the num of samples
            numSamples = (int) (notes.get(i).getDuration() * sampleRate);

            // waves
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

    /**
     * Play sound.
     */
    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.size(),
                AudioTrack.MODE_STATIC);
        audioTrack.write(convertListToByte(generatedSnd), 0, generatedSnd.size());
        audioTrack.play();
    }

    /**
     * Convert ArrayList<Byte> to byte [ ].
     *
     * @param generatedSnd the generated sound
     * @return the byte [ ]
     */
    byte[] convertListToByte(ArrayList<Byte> generatedSnd)
    {
        byte[] arr = new byte[generatedSnd.size()];

        for (int i = 0; i < generatedSnd.size(); i++)
        {
            arr[i] = generatedSnd.get(i);
        }
        return  arr;
    }

    /**
     * Remove note.
     *
     * @param view the view
     */
    public void removeNote(View view) {
        musicNotesView.removeNote();
    }
}
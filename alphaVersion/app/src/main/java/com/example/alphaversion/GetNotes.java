package com.example.alphaversion;

import static android.content.ContentValues.TAG;
import static com.example.alphaversion.FBref.database_ref;
import static com.example.alphaversion.FBref.filesRef;
import static com.example.alphaversion.FBref.musicNotesRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetNotes extends AppCompatActivity {


    private EditText notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_notes);



        notes = (EditText) findViewById(R.id.noteset);



    }

    public void send(View view) {
        database_ref.push().setValue(notes.getText().toString());

        StorageReference voiceRef = filesRef.child("/" +notes.getText().toString() + ".pdf");

        voiceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
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

        if(whatClicked.equals("enterData"))
        {
            si = new Intent(this,MainActivity.class);
            startActivity(si);
        }
        else if(whatClicked.equals("auth"))
        {
            si = new Intent(this,AuthenticationActivity.class);
            startActivity(si);
        }
        else if(whatClicked.equals("play music"))
        {
            si = new Intent(this,playFiles.class);
            startActivity(si);
        }
        else if(whatClicked.equals("fre"))
        {
            si = new Intent(this,Frequency.class);
            startActivity(si);
        }
        else if(whatClicked.equals("getNotes"))
        {
            si = new Intent(this,GetNotes.class);
            startActivity(si);
        }
        return  true;
    }

}

package com.example.betaversion;

import static com.example.betaversion.FBref.filesRef;
import static com.example.betaversion.FBref.mAuth;
import static com.example.betaversion.Node.castFromStringToNote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.util.Assert;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetSection extends AppCompatActivity {
    String IP = "192.168.1.196";
    final int port = 9002;
    EditText et;
    ToggleButton tb;
    String musicNotes;
    String recordPath;
    ProgressDialog progressDialog;
    Uri file = null;
    private TextView filenameText;

    private boolean isRecording = false;
    DatabaseReference privateSectionCase = null;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    private ImageButton recordBtn;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_section);

        tb = (ToggleButton) findViewById(R.id.toggleButton);
        et = (EditText) findViewById(R.id.nickname);

        recordBtn = findViewById(R.id.record_btn);
        timer = findViewById(R.id.record_timer);
        filenameText = findViewById(R.id.fileName);

        musicNotes = "";
    }


    private void sendMessage(final InputStream msg) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setText("192.168.1.0");
        alert.setMessage("Enter the IP of the server");
        alert.setTitle("IP of Server");

        alert.setView(edittext);

        alert.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //OR
                IP = edittext.getText().toString();

                progressDialog = new ProgressDialog(GetSection.this);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setTitle("updating"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);


                new Thread(new Runnable() {
                    public void run() {
                        try {
                            update(msg);

                            GetSection.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(GetSection.this, musicNotes, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                        progressDialog.dismiss();

                    }
                }).start();


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }


    private void update(InputStream msg)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Date date = new Date(); // This object contains the current date value


        String name_of_file = mAuth.getUid() + file.getPath().substring(file.getPath().lastIndexOf("/")+1);

        // get the transcript
        try {
            getTranscript(msg, name_of_file);
        }
        catch (Exception e)
        {
            musicNotes = "";
        }

        // check if it is empty
        if (musicNotes.length() >= 1 && (int)musicNotes.charAt(0) == 65535)
        {
            musicNotes = "";
        }



        Node<Note> n = castFromStringToNote(musicNotes);
        musicNotes = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Section s = new Section(mAuth.getUid(),n,et.getText().toString(), formatter.format(date), tb.isChecked(), name_of_file);

        // update the section
        StorageReference riversRef = filesRef.child(name_of_file);
        riversRef.putFile(file);
        privateSectionCase.child(mAuth.getUid()).push().setValue(s);
    }


    private void getTranscript(InputStream msg, String name_of_file) {
        Client client = new Client();
        try {


            client.startConnection(IP, port);
            OutputStream out = client.getSock().getOutputStream();
            byte[] bytes = new byte[1024];
            InputStream in = msg;
            int count;
            out.write(CodesOfMessages.ADDSECTIONCODE.getBytes(), 0, CodesOfMessages.ADDSECTIONCODE.length());
            while ((count = in.read(bytes)) > 0) {
                out.write("true".getBytes(), 0, 4);

                out.write(bytes, 0, count);
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getSock().getInputStream()));

                // get the more
                for (int i = 0; i < 4; i++)
                {
                    input.read();
                }

            }
            out.write("fals".getBytes(), 0, 4);
            out.write(name_of_file.getBytes(), 0, name_of_file.length());

            BufferedReader input = new BufferedReader(new InputStreamReader(client.getSock().getInputStream()));
            char a = (char) input.read();

            while (input.ready())
            {
                musicNotes += a;
                a = (char) input.read();
            }
            musicNotes += a;





            client.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void recordclick(View view) {
        /*  Check, which button is pressed and do the task accordingly
         */

        if(isRecording) {
            //Stop Recording
            stopRecording();

            // Change button image and set Recording state to false
            recordBtn.setImageDrawable(getDrawableResource(R.drawable.record_btn_stopped));
            isRecording = false;
        }
        else {
            //Check permission to record audio
            if(checkPermissions()) {
                //Start Recording
                startRecording();

                // Change button image and set Recording state to false
                recordBtn.setImageDrawable(getDrawableResource(R.drawable.record_btn_recording));
                isRecording = true;
            }
            else
            {
                Toast.makeText(GetSection.this, "please give permissions", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private Drawable getDrawableResource(int resID) {
        return ContextCompat.getDrawable(this, resID);
    }



    public void submit(View view) throws IOException {
        if (isRecording)
        {
            Toast.makeText(GetSection.this, "please stop the record", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = tb.isChecked() ? "Public Sections" : "Private Sections";


        privateSectionCase = FBref.FBDB.getReference().child(name);
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // upload the file
        if (!recordFile.isEmpty())
        {
            file = Uri.fromFile(new File(recordPath + "/" + recordFile));
        }
        else
        {
            file = Uri.fromFile(new File(recordFile));
        }

        File fileOfAudio = new File(recordPath + "/" + recordFile);
        // Get the size of the file
        InputStream in = new FileInputStream(fileOfAudio);
        sendMessage(in);
//        recognize(loadSoundFileURL(new File(file.getPath())));

    }

    private void stopRecording() {
        //Stop Timer, very obvious
        timer.stop();

        //Change text on page to file saved
        filenameText.setText("Recording Stopped, File Saved : " + recordFile);

        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {
        //Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        //Get app external directory path
        recordPath = GetSection.this.getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + "_" + mAuth.getUid() + ".3gp";

        filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }

    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(GetSection.this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(GetSection.this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording){
            stopRecording();
        }
    }

    public void chooseAudio(View view) {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK && verifyStoragePermissions()){
                //the selected audio.
                Uri file = data.getData();

                recordPath = "";
                recordFile= getRealPathFromURI(file);
                filenameText.setText(recordFile);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public boolean verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(GetSection.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            return true;
        }
        else
        {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    GetSection.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
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


        if(whatClicked.equals("auth"))
        {
            si = new Intent(this, SignInActivity.class);
            startActivity(si);
        }
        return  true;
    }



}
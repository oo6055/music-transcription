package com.example.betaversion;

import static com.example.betaversion.FBref.filesRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.*;
import android.media.*;
import android.os.*;
import android.os.Handler;
import android.view.*;
import android.widget.*;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

/**
 * play the section activity
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  view the notes
 */
public class PlaySection extends AppCompatActivity {

    /**
     * The Media player.
     */
    MediaPlayer mediaPlayer = null;
    /**
     * if there is a music that is playing.
     */
    boolean isPlaying = false;

    /**
     * The File to play.
     */
    File fileToPlay = null;

    /**
     * The Play btn.
     */
    ImageButton playBtn;
    /**
     * The seekbar.
     */
    SeekBar playerSeekbar;
    /**
     * The File name tv.
     */
    TextView fileNameTv;

    /**
     * The Seekbar handler.
     */
    Handler seekbarHandler;
    /**
     * The Update seekbar.
     */
    Runnable updateSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_section);
        playerSeekbar = findViewById(R.id.seekbar);
        playBtn = findViewById(R.id.play_btn);
        fileNameTv = findViewById(R.id.filenametextview);


        Intent gi = getIntent();
        String fileName = gi.getStringExtra("fileName");
        String nickName = gi.getStringExtra("nickname");
        fileNameTv.setText(nickName);


        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

        StorageReference voiceRef = filesRef.child("/" + fileName);

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File finalLocalFile = localFile;
        voiceRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                fileToPlay = finalLocalFile;
                if(isPlaying){
                    stopAudio();
                    playAudio(fileToPlay);
                } else {
                    playAudio(fileToPlay);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    /**
     * pause the audio.
     *
     */
    private void pauseAudio() {
        mediaPlayer.pause();
        playBtn.setImageResource(R.drawable.player_play_btn);
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    /**
     * resume the audio.
     *
     */
    private void resumeAudio() {
        mediaPlayer.start();
        playBtn.setImageResource(R.drawable.player_pause_btn);
        isPlaying = true;

        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    /**
     * stop the audio.
     * it call only in on destroy
     */
    private void stopAudio() {
        //Stop The Audio
        playBtn.setImageResource(R.drawable.player_play_btn);
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    /**
     * play the audio.
     */
    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageResource(R.drawable.player_pause_btn);

        //Play the audio
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    /**
     * update the seekbar.
     */
    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    /**
     * when it destroyes.
     */
    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying) {
            stopAudio();
        }
    }

    /**
     * Play.
     *
     * @param view the view
     */
    public void play(View view) {
        if(isPlaying){
            pauseAudio();
        } else {
            if(fileToPlay != null){
                resumeAudio();
            }
        }
    }
}
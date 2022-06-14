package com.example.betaversion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
/**
 * splash screen
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 10/6/2021  splash screen
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}
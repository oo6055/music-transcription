package com.example.betaversion;

import static com.example.betaversion.FBref.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;

/**
 * this activity is showing my sections
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  sign in to the app
 */
public class SignInActivity extends AppCompatActivity {

    /**
     * The full name of the user
     */
    EditText mFullName, /**
     * The email of the user.
     */
    mEmail, /**
     * The password.
     */
    mPassword, /**
     * The phone.
     */
    mPhone;
    /**
     * The Progress bar.
     */
    ProgressBar progressBar;
    /**
     * The User id.
     */
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);

        progressBar = findViewById(R.id.progressBar);

        if (mAuth.getCurrentUser() != null) {
            finish();
            Intent i = new Intent(getApplicationContext(), ShowMySections.class);
            startActivity(i);
        }

    }

    /**
     * Register to the system
     *
     * @param view the view
     */
    public void register(View view) {

        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        final String fullName = mFullName.getText().toString();
        final String phone = mPhone.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is Required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is Required.");
            return;
        }

        if (password.length() < 6) {
            mPassword.setError("Password Must be >= 6 Characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // register the user in firebase

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // send verification link

                    FirebaseUser fuser = mAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference usersRef = FBref.FBDB.getReference().child("Users");
                            usersRef.child(mAuth.getUid()).setValue(fullName);
                            Intent i = new Intent(getApplicationContext(), ShowMySections.class);
                            startActivity(i);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

                    Toast.makeText(SignInActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                    userID = mAuth.getCurrentUser().getUid();

                } else {
                    Toast.makeText(SignInActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Move to log in.
     *
     * @param view the view
     */
    public void moveToLogIn(View view) {
        startActivity(new Intent(getApplicationContext(), LogInActivity.class));

    }
}
package com.example.alphaversion;

import static android.content.ContentValues.TAG;
import static com.example.alphaversion.FBref.mAuth;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.alphaversion.MainActivity;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationActivity extends AppCompatActivity {
        public static final String TAG = "TAG";
        EditText mFullName,mEmail,mPassword,mPhone;
        Button mRegisterBtn;
        TextView mLoginBtn;
        ProgressBar progressBar;
        String userID;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_auth);

            mFullName   = findViewById(R.id.fullName);
            mEmail      = findViewById(R.id.Email);
            mPassword   = findViewById(R.id.password);
            mPhone      = findViewById(R.id.phone);
            mRegisterBtn= findViewById(R.id.registerBtn);
            mLoginBtn   = findViewById(R.id.createText);

            progressBar = findViewById(R.id.progressBar);

            if(mAuth.getCurrentUser() != null){
                Toast.makeText(this, "already connected", Toast.LENGTH_SHORT).show();
            }


            mRegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    final String fullName = mFullName.getText().toString();
                    final String phone    = mPhone.getText().toString();

                    if(TextUtils.isEmpty(email)){
                        mEmail.setError("Email is Required.");
                        return;
                    }

                    if(TextUtils.isEmpty(password)){
                        mPassword.setError("Password is Required.");
                        return;
                    }

                    if(password.length() < 6){
                        mPassword.setError("Password Must be >= 6 Characters");
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    // register the user in firebase

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                // send verification link

                                FirebaseUser fuser = mAuth.getCurrentUser();
                                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AuthenticationActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                    }
                                });

                                Toast.makeText(AuthenticationActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                                userID = mAuth.getCurrentUser().getUid();

                            }else {
                                Toast.makeText(AuthenticationActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });



            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
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

    public void signout(View view) {
        mAuth.signOut();
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
            si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        else if(whatClicked.equals("auth"))
        {
            si = new Intent(this,AuthenticationActivity.class);
            startActivity(si);
        }

        return  true;
    }


}
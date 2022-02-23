package com.example.betaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

public class LogInActivity extends AppCompatActivity {

    TextView tVtitle;
    EditText eTemail, eTpass;
    CheckBox cBstayconnect;
    Button btn;

    String  email, password;
    Boolean stayConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        tVtitle=(TextView) findViewById(R.id.tVtitle);
        eTemail=(EditText)findViewById(R.id.eTemail);
        eTpass=(EditText)findViewById(R.id.eTpass);
        cBstayconnect=(CheckBox)findViewById(R.id.cBstayconnect);
        btn=(Button)findViewById(R.id.btn);

        stayConnect=false;

        }


    /**
     * Logging in or Registering to the application
     * Using:   Firebase Auth with email & password
     *          Firebase Realtime database with the object User to the branch Users
     * If login or register process is Ok saving stay connect status & pass to next activity
     * <p>
     */
    public void logorreg(View view) {

        email = eTemail.getText().toString();
        password = eTpass.getText().toString();

        final ProgressDialog pd = ProgressDialog.show(this, "Login", "Connecting...", true);
        FBref.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                            editor.commit();
                            Toast.makeText(LogInActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            Intent si = new Intent(LogInActivity.this, ShowMySections.class);
                            startActivity(si);
                        } else {
                            Log.d("MainActivity", "signinUserWithEmail:fail");
                            Toast.makeText(LogInActivity.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
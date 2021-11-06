package com.example.alphaversion;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class AuthenticationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    TextView SignInStatusView;
    private SignInButton SignInBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this/* AppCompatActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        SignInStatusView = (TextView) findViewById(R.id.status);
        SignInBtn = (SignInButton) findViewById(R.id.signInBtn);
//        SetButtons();
        // Get the user's ID and basic profile info using google



        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        // This object is used in order to access the Google API for Signing in



    }



    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }



    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
        SignInStatusView.setText("not connected");

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("\n\n\n\nRequest code: " + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK)
            {
                System.out.println("OK RESULT");
            }
            else
            {
                System.out.println("NOT OK RESULT");
            }
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(null,"handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            SignInStatusView.setText("hello " + acct.getDisplayName());
            updateUI(true);
            SaveClientDetails(acct);
        } else {
            SignInStatusView.setText("not connected");
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            Toast.makeText(this, "signIn", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "signOut", Toast.LENGTH_SHORT).show();
        }
    }


    private void SaveClientDetails(GoogleSignInAccount account)
    {
        // Save to DB
        Log.d(null,"*\nIn SAVE\n*");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Connection failed completely. Sign in and other functions are not available.
        Log.d(null,"ERROR ::: Problems with connection");
    }

    public void signOut(View view) {
        signOut();
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

        return  true;
    }
}
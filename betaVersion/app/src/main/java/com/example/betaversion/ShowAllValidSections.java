package com.example.betaversion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ShowAllValidSections extends AppCompatActivity {

    ListView ls;
    ArrayList<Section> sectionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_valid_sections);

        sectionsList = new ArrayList<Section>();

        ls = (ListView) findViewById(R.id.ls);
        getAllPublicSections();
        ls.setOnCreateContextMenuListener(this);
    }


    private void getAllPublicSections() {


        FBref.FBDB.getReference().child("Public Sections")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            for (DataSnapshot sc : ds.getChildren())
                            {
                                Section s = sc.getValue(Section.class);
                                sectionsList.add(s);
                            }

                        }

                        updateListView(sectionsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (databaseError != null) {
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
     * @return	none
     */
    //@Overrid
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("options");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sectionsoptions, menu);
    }

    /**
     * onContextItemSelected
     * Short description.
     * onContextItemSelected listener use for the ContextMenu
     * <p>
     *     MenuItem item
     *
     * @param  item - the item that selected
     * @return	true if it worked
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String op = item.getTitle().toString();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int i = info.position;
        Intent si;


        if (op.equals("play section"))
        {
            si = new Intent(this,PlaySection.class);
            si.putExtra("fileName",sectionsList.get(i).getNameOfFile());
            si.putExtra("nickname",sectionsList.get(i).getNickName());

            startActivity(si);

        }
        else if (op.equals("get transcript"))
        {
            String pathInFireBase = sectionsList.get(i).getNameOfFile();
            pathInFireBase = pathInFireBase.substring(0,pathInFireBase.indexOf(".")) + ".pdf";
            StorageReference pdfRef = FBref.filesRef.child("/" + pathInFireBase);
            pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUrl) {
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

        return true;
    }

    private void updateListView(ArrayList<Section> sectionsList) {

        MyListAdapter adapter=new MyListAdapter(this,sectionsList);
        ls.setAdapter(adapter);

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

        if(whatClicked.equals("show my sections"))
        {
            si = new Intent(this,ShowMySections.class);
            startActivity(si);
        }
        else if(whatClicked.equals("signout"))
        {
            FBref.mAuth.signOut();
            si = new Intent(this, SignInActivity.class);
            startActivity(si);
        }

        return  true;
    }

}
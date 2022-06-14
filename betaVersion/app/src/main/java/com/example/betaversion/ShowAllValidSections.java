package com.example.betaversion;

import androidx.annotation.NonNull;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * this activity is showing all the valid sections
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  this activity is showing all the valid sections
 */
public class ShowAllValidSections extends AppCompatActivity {

    /**
     * The list view
     */
    ListView ls;
    /**
     * The Sections list.
     */
    ArrayList<Section> sectionsList;
    /**
     * The navigation btn.
     */
    BottomNavigationView btnnav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_valid_sections);

        sectionsList = new ArrayList<Section>();
        btnnav = (BottomNavigationView) findViewById(R.id.btnnav);
        btnnav.setSelectedItemId(R.id.publicsec);

        btnnav.setOnNavigationItemSelectedListener(bottomNavMethod);


        ls = (ListView) findViewById(R.id.ls);
        getAllPublicSections();
        ls.setOnCreateContextMenuListener(this);
    }

    /**
     * get the the public sections
     *
     */
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

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getTitle().equals("show my sections")) {
                        Intent si = new Intent(ShowAllValidSections.this, ShowMySections.class);
                        startActivity(si);
                    }
                    return false;
                }
            };

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
        else if (op.equals("change section"))
        {
            si = new Intent(this,ChangeNotes.class);
            si.putExtra("fileName",sectionsList.get(i).getNameOfFile());
            si.putExtra("uid",sectionsList.get(i).getUid());
            si.putExtra("privacy",sectionsList.get(i).getPublicOrPrivate());


            startActivity(si);
        }
        else if (op.equals("delete section"))
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query deleteQuery = ref.child(sectionsList.get(i).getPublicOrPrivate() ? "Public Sections" : "Private Sections").child(sectionsList.get(i).getUid()).orderByChild("date").equalTo(sectionsList.get(i).getDate());

            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            getAllPublicSections();
        }

        return true;
    }

    /**
     * get the the public sections
     *
     * @param  sectionsList the list of sections that we want to show
     */
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
package com.example.betaversion;

import static com.example.betaversion.FBref.mAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.example);
    }

    public void submit(View view) {

        String text = tv.getText().toString();
        Node<Note> head = new Node<Note>(null,null);
        Node<Note> next = head;

        for(int i = 0; i < (text.length() / 2 + 1); i = i + 2)
        {
            // just example
            Note n = new Note(text.substring(i, i+2),1,440);
            next.setElement(n);
            next.setNext(new Node<Note>(null,null));
            next = next.getNext();
        }


        DatabaseReference privateSectionCase = FBref.FBDB.getReference().child("Private Sections");
        Section s = new Section(mAuth.getUid(), head,"first", "now", false);
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
        }
        else
        {
            privateSectionCase.child(mAuth.getUid()).push().setValue(s);
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
            si = new Intent(this,SignItActivity.class);
            startActivity(si);
        }
        return  true;
    }

    public void check(View view) {

        DatabaseReference privateSectionCase = FBref.FBDB.getReference().child("Private Sections");
        privateSectionCase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Section sec = dataSnapshot.getValue(Section.class);
                System.out.println("data = " + sec.getComposition().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
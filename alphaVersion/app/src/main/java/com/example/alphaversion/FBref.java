package com.example.alphaversion;


import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * The FBRef activity.
 *
 *  @author Ori Ofek <oriofek106@gmail.com> 17/04/2021
 *  @version 1.0
 *  @since 17/04/2021
 *  sort description:
 *  this is the activty the implement the exercise that my teacher gave and in this activity there is a connection to the fireBase
 */
public class FBref {

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static DatabaseReference refUsers=FBDB.getReference("Users");
    private static FirebaseStorage  storage  = FirebaseStorage.getInstance();
    public static StorageReference filesRef = storage.getReference("files");
}

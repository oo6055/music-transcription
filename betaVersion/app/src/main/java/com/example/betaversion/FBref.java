package com.example.betaversion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * The FBref static class
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  The firebase objects.
 */
public class FBref {
    /**
     * the firebase database
     */
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    /**
     * The authentication of firebase.
     */
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    /**
     * The storage of firebase.
     */
    private static FirebaseStorage  storage  = FirebaseStorage.getInstance();
    /**
     * The files.
     */
    public static StorageReference filesRef = storage.getReference("files");
}

package com.example.betaversion;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseStorage  storage  = FirebaseStorage.getInstance();
    public static StorageReference filesRef = storage.getReference("files");

}

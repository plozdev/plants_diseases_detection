package com.example.plantdiseasedetection.utils;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

/**
 * Utility class for Firebase operations
 */
public class FirebaseUtils {
    private static final String TAG = "FirebaseUtils";

    /**
     * Check if a user is currently signed in
     *
     * @return true if user is signed in, false otherwise
     */
    public static boolean isUserSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    /**
     * Get the current user's ID
     *
     * @return User ID or null if not signed in
     */
    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * Get a reference to a document in Firestore
     *
     * @param collection Collection name
     * @param documentId Document ID
     * @return DocumentReference object
     */
    public static DocumentReference getDocumentReference(String collection, String documentId) {
        return FirebaseFirestore.getInstance().collection(collection).document(documentId);
    }


    /**
     * Sign out the current user
     */
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }


}

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
     * Upload a file to Firebase Storage
     *
     * @param file File to upload
     * @param storageDirectory Directory in Firebase Storage
     * @param callback Callback to handle success or failure
     */
    public static void uploadFile(File file, String storageDirectory, UploadCallback callback) {
        if (file == null || !file.exists()) {
            callback.onFailure(new IllegalArgumentException("File does not exist"));
            return;
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getName();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child(storageDirectory + "/" + fileName);

        UploadTask uploadTask = fileRef.putFile(Uri.fromFile(file));

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                callback.onSuccess(downloadUri.toString());
            } else {
                Log.e(TAG, "Upload failed", task.getException());
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Delete a file from Firebase Storage
     *
     * @param fileUrl URL of the file to delete
     * @param callback Callback to handle success or failure
     */
    public static void deleteFile(String fileUrl, DeleteCallback callback) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid file URL"));
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
        storageRef.delete().addOnSuccessListener(aVoid -> {
            callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Delete failed", e);
            callback.onFailure(e);
        });
    }

    /**
     * Sign out the current user
     */
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Interface for upload success/failure callbacks
     */
    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
    }

    /**
     * Interface for delete success/failure callbacks
     */
    public interface DeleteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}

package com.example.plantdiseasedetection.fragments;
import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantdiseasedetection.BuildConfig;
import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.databinding.FragmentScanBinding;
import com.example.plantdiseasedetection.model.Plant;
import com.example.plantdiseasedetection.model.ScanResult;
import com.example.plantdiseasedetection.services.GeminiApiClient;
import com.example.plantdiseasedetection.utils.ImageUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ScanFragment extends Fragment {
    private FragmentScanBinding binding;
    private Uri imageUri;
    private Bitmap imageBitmap;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnScan.setOnClickListener(v -> dispatchTakePictureIntent());
        binding.btnSelectImage.setOnClickListener(v -> openGallery());
        binding.btnAnalyze.setOnClickListener(v -> {
            if (validateInput()) analyzePlant();
        });
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Storage success");
    }

    private void showPreview(boolean show) {
        binding.btnScan.setEnabled(!show);
        binding.btnSelectImage.setEnabled(!show);
        binding.btnAnalyze.setEnabled(show);
        binding.scan.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.preview.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnAnalyze.setEnabled(!isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }






    //SCAN -----------------------------------------------
    private static final int CAMERA_REQUEST_CODE = 100;

    private void dispatchTakePictureIntent() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            //Request permission
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            imageResultLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(getContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed
                dispatchTakePictureIntent();
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imageResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.getExtras() != null && data.getExtras().get("data") != null) {
                        // Camera image
                        showPreview(true);
                        imageBitmap = (Bitmap) data.getExtras().get("data");
                        imageUri = null;
                        Glide.with(this).load(imageBitmap).into(binding.ivPlantImage);
                    } else if (data.getData() != null) {
                        // Gallery image

                        imageUri = data.getData();
                        imageBitmap = null;
                        try {
                            showPreview(true);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                            Glide.with(requireContext()).load(bitmap).into(binding.ivPlantImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
//                    binding.ivPlantImage.setVisibility(VISIBLE);
                }
            });







    //ANALYZE---------------------------------------------------------------------

    private boolean validateInput() {
        if (imageBitmap == null && imageUri == null) {
            Toast.makeText(requireContext(), R.string.image_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.etPlantType.getText().toString().trim().isEmpty()) {
            binding.etPlantType.setError("Required");
            return false;
        }

        return true;
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void analyzePlant() {
        showLoading(true);
        // Create plant object
        Plant plant = new Plant();
        plant.setType(binding.etPlantType.getText().toString().trim());
        plant.setAge(binding.etPlantAge.getText().toString().trim());
        plant.setGrowingConditions(binding.etGrowingConditions.getText().toString().trim());
        if (imageBitmap != null) {
            plant.setImageB64(ImageUtils.bitmapToBase64(imageBitmap));
        } else if (imageUri != null) {
            plant.setImageB64(ImageUtils.bitmapToBase64(ImageUtils.uriToBitmap(requireContext(),imageUri)));
        }


        Log.d("PlantUpload", "Plant image set done!" + plant.getImageB64());
        getUserRegion(region -> {
            if (region != null) plant.setLocation(region);
        });
        ScanResult currentScanResult = new ScanResult();
        currentScanResult.setId(UUID.randomUUID().toString());
        currentScanResult.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentScanResult.setPlant(plant);
        currentScanResult.setDiseaseDetected(getString(R.string.disease));
        currentScanResult.setTreatmentRecommendations(getString(R.string.treatment));
        currentScanResult.setPreventativeMeasures(getString(R.string.preventative));
        currentScanResult.setTimestamp(Timestamp.now());

        saveResultToFirestore(currentScanResult);

        uploadImageToFirebase(new OnImageUploadListener() {
            @Override
            public void onSuccess(String url) {

                Intent i = new Intent(requireContext(), ResultActivity.class);
                startActivity(i);
                /**
                 * Can't run now
                 * @param plant
                 */
//                callGeminiApi(plant);
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void uploadImageToFirebase(OnImageUploadListener listener) {
        if (imageUri == null && imageBitmap == null) {
           listener.onFailure(new IllegalArgumentException("No image selected"));
           return;
        }

        StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask;

        if (imageUri != null) {
            uploadTask = fileRef.putFile(imageUri);
        } else {
            // Convert Bitmap to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();
            uploadTask = fileRef.putBytes(imageData);
        }

        uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            Log.d("PlantUpload", "Image uploaded successfully: " + downloadUrl);
            listener.onSuccess(downloadUrl);
        })).addOnFailureListener(e -> {
            Log.e("PlantUpload", "Image upload failed: " + e.getMessage());
            listener.onFailure(e);
        });
}

    private interface OnImageUploadListener {
        void onSuccess(String imageUrl);

        void onFailure(Exception e);
    }

    private void callGeminiApi(Plant plant) {
        String apiKey = BuildConfig.GEMINI_API_KEY; // Replace with your real API key from environment

        // Create the prompt for Gemini
        StringBuilder prompt = new StringBuilder();
        String region = (plant.getLocation() != null && !plant.getLocation().isEmpty()) ? plant.getLocation() + " Vietnam" : " Vietnam";
        String weather = requireActivity().getSharedPreferences("weather", Activity.MODE_PRIVATE).getString("weather", "sunny");
        prompt.append("I'm a farmer in ");
        prompt.append(region);
        prompt.append(". Analyze this plant image and detect any diseases it may have.");


        prompt.append("Plant type: ").append(plant.getType()).append(". ");

        if (!plant.getAge().isEmpty()) {
            prompt.append("Plant age (months): ").append(plant.getAge()).append(" months. ");
        }

        if (!plant.getGrowingConditions().isEmpty()) {
            prompt.append("Growing conditions: ").append(plant.getGrowingConditions()).append(". ");
        }

        String prompt1 =
                "\n1. Low-cost and locally available materials and methods." +
                        "\n2. Immediate treatments (e.g., pesticides, pruning, biological controls)." +
                        "\n3. Long-term management strategies (crop rotation, resistant varieties)." +
                        "\n4. Preventive measures suitable for this region to minimize future risks.";

        prompt.append("Based on the current weather conditions (");
        prompt.append(weather);
        prompt.append(") and the regional characteristics of (");
        prompt.append(region);
        prompt.append("), please provide detailed recommendations for Integrated Pest Management (IPM), prioritizing:");
        prompt.append(prompt1);
        prompt.append("Make your response clear, concise, and directly applicable to farmers. Format your response with these three sections: \n1) Disease Detected\n2) Treatment Recommendations\n3) Preventative Measures.");

        GeminiApiClient.getInstance().analyzeImage(apiKey, prompt.toString(), plant.getImageB64(), new GeminiApiClient.GeminiResponseCallback() {
            @Override
            public void onSuccess(GeminiApiClient.GeminiResponse response) {
                // Process and display results
                showLoading(false);

                Toast.makeText(requireContext(), "Analysis successful", Toast.LENGTH_SHORT).show();
//                processAnalysisResults(response, plant);
            }

            @Override
            public void onFailure(Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Analysis failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveResultToFirestore(ScanResult scanResult) {
        firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("scan_results")
                .document(scanResult.getId())
                .set(scanResult)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Scan result saved to Firestore"))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving scan result", e));
    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getUserRegion(RegionCallback callback) {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        String region = addresses.get(0).getAdminArea();
                        callback.onRegionDetected(region);
                    } else {
                        callback.onRegionDetected(null);
                    }
                } catch (Exception e) {
                    callback.onRegionDetected(null);
                }
            } else {
                callback.onRegionDetected(null);
            }
        });
        // Callback cho lấy vùng

    }
    interface RegionCallback {
        void onRegionDetected(String region);
    }
}

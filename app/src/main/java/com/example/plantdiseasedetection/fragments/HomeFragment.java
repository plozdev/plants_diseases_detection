package com.example.plantdiseasedetection.fragments;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantdiseasedetection.BuildConfig;
import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.databinding.FragmentHomeBinding;
import com.example.plantdiseasedetection.model.ScanResult;
import com.example.plantdiseasedetection.model.WeatherResponse;
import com.example.plantdiseasedetection.services.APIClient;
import com.example.plantdiseasedetection.services.WeatherService;
import com.example.plantdiseasedetection.utils.FirebaseUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding; // ViewBinding
    private FirebaseFirestore firestore;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        getWeatherInfo();
        fetchRecentActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        binding.btnScan.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_home_to_scan));
        binding.btnChat.setOnClickListener(v -> navController.navigate(R.id.action_home_to_chat));

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Exit the app
                            requireActivity().finishAffinity();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog
                            dialog.dismiss();
                        })
                        .show();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getWeatherInfo();
        fetchRecentActivity();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private void fetchRecentActivity() {
        String userId = FirebaseUtils.getCurrentUserId();
        firestore.collection("users")
                .document(userId)
                .collection("scan_results")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Lấy theo thứ tự mới nhất
                .limit(1) // Chỉ lấy kết quả mới nhất
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        ScanResult scanResult = document.toObject(ScanResult.class);

                        // Hiển thị kết quả lên giao diện
                        if (scanResult != null) {
                            String recentResultText = "🌿 Plant: " + scanResult.getPlant().getType() +
                                    "\n⚠️ Disease: " + "Healthy" +
//                                    scanResult.getDiseaseDetected() + -- fake data
                                    "\n⏰ Scanned on: " + scanResult.getTimestamp().toDate();
                            binding.recentActivityTxt.setText(recentResultText);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error fetching recent activity", e);
                    binding.recentActivityTxt.setText("Error fetching recent activity");
                });
    }







    //Method get weather and save in SharePreference
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private void getWeatherInfo() {
        if (!isLocationEnabled()) {
            // Show request turn on GPS
            new AlertDialog.Builder(requireContext())
                    .setTitle("Enable Location")
                    .setMessage("Please enable location services to get weather information.")
                    .setPositiveButton("Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission accepted, get location
            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(requireContext());
            locationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    fetchWeather(location.getLatitude(), location.getLongitude());
                } else {
                    updateWeatherUI("Unable to get location");
                }
            }).addOnFailureListener(e ->updateWeatherUI("Unable to get location"));

        } else {
            // Request permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchWeather(double lat, double lon) {
        WeatherService weatherService = APIClient.getClient().create(WeatherService.class);
        weatherService.getCurrentWeather(lat, lon, BuildConfig.WEATHER_API_KEY, "metric")
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherResponse weather = response.body();
                            String description = weather.getWeather()[0].getDescription();
                            String temp = weather.getMain().getTemp() + "°C";
                            String weatherInfo = description + ", " + temp;
                            weatherInfo = weatherInfo.substring(0,1).toUpperCase()+weatherInfo.substring(1);
                            // Saved in SharedPreferences
                            requireActivity().getSharedPreferences("weather_info", MODE_PRIVATE)
                                    .edit()
                                    .putString("weather",weatherInfo )
                                    .apply();

                            updateWeatherUI(weatherInfo);
                        } else {
                            updateWeatherUI("Weather data not found");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
//                        updateWeatherUI("Unable to load weather data");
                        if (!isAdded()) {
                            return;
                        }

                        // Handle error
                        requireActivity().runOnUiThread(() -> {
                            updateWeatherUI("Weather unavailable");
                        });

                    }
                });
    }
    private void updateWeatherUI(String weatherText) {
        if (binding != null && isAdded()) {
            binding.localWeatherTxt.setText(weatherText);
            if (weatherText.contains("Rainy")  || weatherText.contains("rainy") ||
                    weatherText.contains("Thunderstorm") || weatherText.contains("thunderstorm"))
                binding.weatherIcon.setImageResource(R.drawable.ic_rain);
        }

    }



}
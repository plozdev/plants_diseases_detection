package com.example.plantdiseasedetection.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.databinding.FragmentHomeBinding;
import com.example.plantdiseasedetection.model.WeatherResponse;
import com.example.plantdiseasedetection.services.APIClient;
import com.example.plantdiseasedetection.services.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding; // ViewBinding

    public HomeFragment() {}

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
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecentActivity();
        getWeatherInfo();
    }

    private void updateRecentActivity() {
        String lastResult = requireActivity()
                .getSharedPreferences("recent_activity", MODE_PRIVATE)
                .getString("last_result", getString(R.string.recent_activity_null));
        binding.recentActivityTxt.setText(lastResult);
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
        weatherService.getCurrentWeather(lat, lon, getString(R.string.weather_api_key), "metric")
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherResponse weather = response.body();
                            String description = weather.getWeather()[0].getDescription();
                            String temp = weather.getMain().getTemp() + "°C";
                            String weatherInfo = description + ", " + temp;

                            // Saved in SharedPreferences
                            requireActivity().getSharedPreferences("weather_info", MODE_PRIVATE)
                                    .edit()
                                    .putString("weather", weatherInfo)
                                    .apply();

                            updateWeatherUI(weatherInfo);
                        } else {
                            updateWeatherUI("Weather data not found");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                        updateWeatherUI("Unable to load weather data");
                    }
                });
    }
    private void updateWeatherUI(String weatherText) {
        binding.localWeatherTxt.setText(weatherText);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.plantdiseasedetection.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.databinding.FragmentHomeBinding;

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

        binding.btnScan.setOnClickListener(v -> navController.navigate(R.id.action_home_to_scan));
        binding.btnLookup.setOnClickListener(v -> navController.navigate(R.id.action_home_to_lookup));
        binding.btnChat.setOnClickListener(v -> navController.navigate(R.id.action_home_to_chat));
        binding.btnSchedule.setOnClickListener(v -> navController.navigate(R.id.action_home_to_schedule));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecentActivity();
    }

    private void updateRecentActivity() {
        String lastResult = requireActivity()
                .getSharedPreferences("recent activity", MODE_PRIVATE)
                .getString("last result", String.valueOf(R.string.recent_activity_null));
        binding.recentActivityTxt.setText(lastResult);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
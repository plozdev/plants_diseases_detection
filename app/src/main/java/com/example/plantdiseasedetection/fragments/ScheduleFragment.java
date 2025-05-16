package com.example.plantdiseasedetection.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.databinding.FragmentScheduleBinding;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.deactive.setVisibility(View.VISIBLE);
                binding.btnBack.setOnClickListener(v->{
                    Navigation.findNavController(v).navigate(R.id.action_scheduleFragment_to_homeFragment);
                });
            }
        },400);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
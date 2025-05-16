package com.example.plantdiseasedetection.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantdiseasedetection.BuildConfig;
import com.example.plantdiseasedetection.MainActivity;
import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.SplashActivity;
import com.example.plantdiseasedetection.databinding.FragmentProfileBinding;
import com.example.plantdiseasedetection.utils.FirebaseUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user
        if (user != null) {
            // Set user information
            Glide.with(this).load(user.getPhotoUrl()).circleCrop().error(R.drawable.ic_profile_placeholder).into(binding.imgAvatar);
            binding.tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "No Name");
            binding.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "No Email");
        } else {
            Glide.with(this).load(R.drawable.ic_profile_placeholder).circleCrop().into(binding.imgAvatar);
            binding.tvName.setText("Guest");
            binding.tvEmail.setText("Not logged in");
        }

        // Logout button logic
        binding.btnLogout.setOnClickListener(v -> {
            signOut(requireContext());
            startActivity(new Intent(requireContext(), SplashActivity.class));
            requireActivity().finish();
            // Add redirection to login screen if needed
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    public static void signOut(Context context) {
        FirebaseUtils.signOut();

        // Đăng xuất GoogleSignInClient để đảm bảo không auto đăng nhập lại
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Log.d("SignOut", "Successfully signed out from Google");
        });
    }
}
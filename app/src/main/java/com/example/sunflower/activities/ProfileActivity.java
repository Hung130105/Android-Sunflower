package com.example.sunflower.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sunflower.R;
import com.example.sunflower.models.User;
import com.example.sunflower.utils.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvFullname, tvEmail;
    private Button btnLogout;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = SharedPrefManager.getInstance(this).getUser();

        initViews();
        displayUserInfo();
        setupListeners();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void displayUserInfo() {
        tvUsername.setText(currentUser.getUsername());
        tvFullname.setText(currentUser.getFullname());
        tvEmail.setText("Chưa cập nhật");
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    SharedPrefManager.getInstance(this).logout();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
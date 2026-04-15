package com.example.sunflower.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunflower.R;
import com.example.sunflower.adapters.ExamAdapter;
import com.example.sunflower.api.ApiService;
import com.example.sunflower.api.RetrofitClient;
import com.example.sunflower.models.DeThi;
import com.example.sunflower.models.User;
import com.example.sunflower.utils.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvWelcome;
    private RecyclerView rvExams;
    private ExamAdapter examAdapter;
    private List<DeThi> examList;
    private CardView cardDictionary, cardFlashcard, cardHistory;
    private Button btnViewAll;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra đăng nhập
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Khôi phục token
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token != null && !token.isEmpty()) {
            RetrofitClient.setAuthToken(token);
        }

        currentUser = SharedPrefManager.getInstance(this).getUser();

        initViews();
        setupNavigationDrawer();
        setupClickListeners();
        loadExams();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tvWelcome = findViewById(R.id.tvWelcome);
        rvExams = findViewById(R.id.rvExams);
        cardDictionary = findViewById(R.id.cardDictionary);
        cardFlashcard = findViewById(R.id.cardFlashcard);
        cardHistory = findViewById(R.id.cardHistory);
        btnViewAll = findViewById(R.id.btnViewAll);

        tvWelcome.setText("Chào mừng, " + (currentUser != null ? currentUser.getFullname() : "Học viên") + "!");

        examList = new ArrayList<>();
        examAdapter = new ExamAdapter(examList, exam -> {
            Intent intent = new Intent(MainActivity.this, TakeExamActivity.class);
            intent.putExtra("exam_id", exam.getMaDeThi());
            intent.putExtra("exam_name", exam.getTenDeThi());
            intent.putExtra("duration", exam.getThoiGianLam());
            startActivity(intent);
        });
        rvExams.setLayoutManager(new LinearLayoutManager(this));
        rvExams.setAdapter(examAdapter);
    }

    private void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    drawerLayout.closeDrawers();
                } else if (id == R.id.nav_exam) {
                    startActivity(new Intent(MainActivity.this, ExamActivity.class));
                } else if (id == R.id.nav_dictionary) {
                    startActivity(new Intent(MainActivity.this, DictionaryActivity.class));
                } else if (id == R.id.nav_flashcard) {
                    startActivity(new Intent(MainActivity.this, FlashcardActivity.class));
                } else if (id == R.id.nav_history) {
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else if (id == R.id.nav_logout) {
                    logout();
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    SharedPrefManager.getInstance(this).logout();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setupClickListeners() {
        cardDictionary.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DictionaryActivity.class));
        });

        cardFlashcard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FlashcardActivity.class));
        });

        cardHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        btnViewAll.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ExamActivity.class));
        });
    }

    private void loadExams() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<DeThi>> call = apiService.getExams();

        call.enqueue(new Callback<List<DeThi>>() {
            @Override
            public void onResponse(Call<List<DeThi>> call, Response<List<DeThi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    examList.clear();
                    List<DeThi> all = response.body();
                    int limit = Math.min(3, all.size());
                    examList.addAll(all.subList(0, limit));
                    examAdapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    Toast.makeText(MainActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(MainActivity.this).logout();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<DeThi>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
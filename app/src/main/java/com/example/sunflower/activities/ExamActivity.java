package com.example.sunflower.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunflower.R;
import com.example.sunflower.adapters.ExamAdapter;
import com.example.sunflower.api.ApiService;
import com.example.sunflower.api.RetrofitClient;
import com.example.sunflower.models.DeThi;
import com.example.sunflower.utils.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView rvExams;
    private ExamAdapter examAdapter;
    private List<DeThi> examList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        // Khởi tạo views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        rvExams = findViewById(R.id.rvExams);

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đề thi TOEIC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // Setup Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ExamActivity.this, MainActivity.class));
            } else if (id == R.id.nav_exam) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_dictionary) {
                startActivity(new Intent(ExamActivity.this, DictionaryActivity.class));
            } else if (id == R.id.nav_flashcard) {
                startActivity(new Intent(ExamActivity.this, FlashcardActivity.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(ExamActivity.this, HistoryActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(ExamActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_logout) {
                SharedPrefManager.getInstance(this).logout();
                startActivity(new Intent(ExamActivity.this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Khôi phục token
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token != null && !token.isEmpty()) {
            RetrofitClient.setAuthToken(token);
        }

        initViews();
        loadExams();
    }

    private void initViews() {
        examList = new ArrayList<>();
        examAdapter = new ExamAdapter(examList, exam -> {
            Intent intent = new Intent(ExamActivity.this, ChooseModeActivity.class);
            intent.putExtra("exam_id", exam.getMaDeThi());
            intent.putExtra("exam_name", exam.getTenDeThi());
            intent.putExtra("duration", exam.getThoiGianLam());
            startActivity(intent);
        });
        rvExams.setLayoutManager(new LinearLayoutManager(this));
        rvExams.setAdapter(examAdapter);
    }

    private void loadExams() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<DeThi>> call = apiService.getExams();

        call.enqueue(new Callback<List<DeThi>>() {
            @Override
            public void onResponse(Call<List<DeThi>> call, Response<List<DeThi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    examList.clear();
                    examList.addAll(response.body());
                    examAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ExamActivity.this, "Không có đề thi nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DeThi>> call, Throwable t) {
                Toast.makeText(ExamActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
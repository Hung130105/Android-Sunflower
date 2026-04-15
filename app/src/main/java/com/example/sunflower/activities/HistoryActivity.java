package com.example.sunflower.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunflower.R;
import com.example.sunflower.adapters.HistoryAdapter;
import com.example.sunflower.api.ApiService;
import com.example.sunflower.api.RetrofitClient;
import com.example.sunflower.models.HistorySession;
import com.example.sunflower.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private List<HistorySession> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token != null && !token.isEmpty()) {
            RetrofitClient.setAuthToken(token);
        }

        initViews();
        loadHistory();
    }

    private void initViews() {
        rvHistory = findViewById(R.id.rvHistory);
        historyList = new ArrayList<>();

        // ✅ Thêm sự kiện click để xem chi tiết bài làm
        historyAdapter = new HistoryAdapter(historyList, session -> {
            Intent intent = new Intent(HistoryActivity.this, ExamReviewActivity.class);
            intent.putExtra("session_id", session.getMaPhien());
            intent.putExtra("exam_name", session.getTenDeThi());
            intent.putExtra("score", session.getDiemSo());
            intent.putExtra("lc_score", session.getDiemLC());
            intent.putExtra("rc_score", session.getDiemRC());
            intent.putExtra("correct", session.getSoCauDung());
            intent.putExtra("wrong", session.getSoCauSai());
            intent.putExtra("skipped", session.getSoCauKhongChon());
            startActivity(intent);
        });

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);
    }

    private void loadHistory() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<HistorySession>> call = apiService.getHistory();

        call.enqueue(new Callback<List<HistorySession>>() {
            @Override
            public void onResponse(Call<List<HistorySession>> call, Response<List<HistorySession>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    historyList.clear();
                    historyList.addAll(response.body());
                    historyAdapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    Toast.makeText(HistoryActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(HistoryActivity.this).logout();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<HistorySession>> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Lỗi tải lịch sử: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
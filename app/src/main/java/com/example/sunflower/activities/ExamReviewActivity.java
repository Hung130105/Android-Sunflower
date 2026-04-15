package com.example.sunflower.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.example.sunflower.R;
import com.example.sunflower.api.ApiService;
import com.example.sunflower.api.RetrofitClient;
import com.example.sunflower.models.CauHoi;
import com.example.sunflower.models.DapAn;
import com.example.sunflower.models.SessionDetailResponse;
import com.example.sunflower.utils.SharedPrefManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamReviewActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout llQuestions;
    private TextView tvExamName, tvScore, tvLC, tvRC, tvStats;
    private Button btnBack;

    private int sessionId;
    private String examName;
    private int totalScore, lcScore, rcScore;
    private int correct, wrong, skipped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_review);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Xem lại bài làm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionId = getIntent().getIntExtra("session_id", 0);
        examName = getIntent().getStringExtra("exam_name");
        totalScore = getIntent().getIntExtra("score", 0);
        lcScore = getIntent().getIntExtra("lc_score", 0);
        rcScore = getIntent().getIntExtra("rc_score", 0);
        correct = getIntent().getIntExtra("correct", 0);
        wrong = getIntent().getIntExtra("wrong", 0);
        skipped = getIntent().getIntExtra("skipped", 0);

        initViews();
        displaySummary();
        loadSessionDetail();
    }

    private void initViews() {
        scrollView = findViewById(R.id.scrollView);
        llQuestions = findViewById(R.id.llQuestions);
        tvExamName = findViewById(R.id.tvExamName);
        tvScore = findViewById(R.id.tvScore);
        tvLC = findViewById(R.id.tvLC);
        tvRC = findViewById(R.id.tvRC);
        tvStats = findViewById(R.id.tvStats);
        btnBack = findViewById(R.id.btnBack);

        tvExamName.setText(examName != null ? examName : "Bài làm");

        btnBack.setOnClickListener(v -> finish());
    }

    private void displaySummary() {
        tvScore.setText(totalScore + " điểm");
        tvLC.setText("Listening: " + lcScore + "/495");
        tvRC.setText("Reading: " + rcScore + "/495");

        String stats = "✅ Đúng: " + correct + "   |   ❌ Sai: " + wrong + "   |   ⏭️ Bỏ qua: " + skipped;
        tvStats.setText(stats);

        if (totalScore >= 800) {
            tvScore.setTextColor(ContextCompat.getColor(this, R.color.sunflower_green));
        } else if (totalScore >= 600) {
            tvScore.setTextColor(ContextCompat.getColor(this, R.color.sunflower_orange));
        } else {
            tvScore.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    private void loadSessionDetail() {
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token != null && !token.isEmpty()) {
            RetrofitClient.setAuthToken(token);
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<SessionDetailResponse> call = apiService.getSessionDetail(sessionId);

        call.enqueue(new Callback<SessionDetailResponse>() {
            @Override
            public void onResponse(Call<SessionDetailResponse> call, Response<SessionDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SessionDetailResponse detail = response.body();
                    if (detail.getChiTiet() != null) {
                        displayQuestions(detail.getChiTiet());
                    } else {
                        Toast.makeText(ExamReviewActivity.this, "Không có dữ liệu câu hỏi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ExamReviewActivity.this, "Không thể tải chi tiết bài làm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SessionDetailResponse> call, Throwable t) {
                Toast.makeText(ExamReviewActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestions(List<CauHoi> questions) {
        if (questions == null || questions.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Không có dữ liệu câu hỏi");
            tvEmpty.setPadding(16, 16, 16, 16);
            llQuestions.addView(tvEmpty);
            return;
        }

        for (CauHoi q : questions) {
            View questionView = createQuestionView(q);
            llQuestions.addView(questionView);
        }
    }

    private View createQuestionView(CauHoi q) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 16, 16, 16);

        container.setBackground(ContextCompat.getDrawable(this, android.R.drawable.editbox_background));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        container.setLayoutParams(params);

        TextView tvNumber = new TextView(this);
        tvNumber.setText("Câu " + q.getSTT() + " (Part " + q.getTenPart() + ")");
        tvNumber.setTextSize(14);
        tvNumber.setTextColor(ContextCompat.getColor(this, R.color.sunflower_orange));
        tvNumber.setPadding(0, 0, 0, 8);
        container.addView(tvNumber);

        TextView tvContent = new TextView(this);
        String content = q.getNoiDung();
        if (content == null || content.isEmpty()) {
            content = "📢 Nghe audio để trả lời câu hỏi";
        }
        tvContent.setText(content);
        tvContent.setTextSize(16);
        tvContent.setPadding(0, 0, 0, 12);
        container.addView(tvContent);

        List<DapAn> answers = q.getDap_an();
        Integer selectedId = q.getDapAnChonId();
        Integer correctId = q.getDapAnDungId();

        if (answers != null) {
            for (DapAn ans : answers) {
                Button btnAnswer = new Button(this);
                btnAnswer.setText(ans.getKyHieu() + ". " + ans.getNoiDung());
                btnAnswer.setAllCaps(false);
                btnAnswer.setTextSize(14);

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                btnParams.setMargins(0, 0, 0, 8);
                btnAnswer.setLayoutParams(btnParams);
                btnAnswer.setPadding(32, 16, 32, 16);
                btnAnswer.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL);

                if (selectedId != null && selectedId == ans.getMaDapAn()) {
                    if (ans.isCorrect()) {
                        btnAnswer.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.sunflower_green));
                        btnAnswer.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    } else {
                        btnAnswer.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
                        btnAnswer.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    }
                } else if (correctId != null && correctId == ans.getMaDapAn()) {
                    btnAnswer.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
                    btnAnswer.setTextColor(ContextCompat.getColor(this, R.color.black));
                } else {
                    btnAnswer.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_gray));
                    btnAnswer.setTextColor(ContextCompat.getColor(this, R.color.black));
                }

                btnAnswer.setClickable(false);
                container.addView(btnAnswer);
            }
        }

        if (q.getGiaiThich() != null && !q.getGiaiThich().isEmpty()) {
            TextView tvExplain = new TextView(this);
            tvExplain.setText("💡 Giải thích: " + q.getGiaiThich());
            tvExplain.setTextSize(12);
            tvExplain.setTextColor(ContextCompat.getColor(this, R.color.purple_500));
            tvExplain.setPadding(0, 12, 0, 0);
            container.addView(tvExplain);
        }

        return container;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
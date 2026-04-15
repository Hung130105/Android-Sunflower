package com.example.sunflower.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sunflower.R;
import com.example.sunflower.adapters.PaletteAdapter;
import com.example.sunflower.api.ApiService;
import com.example.sunflower.api.RetrofitClient;
import com.example.sunflower.api.request.SubmitRequest;
import com.example.sunflower.models.CauHoi;
import com.example.sunflower.models.DapAn;
import com.example.sunflower.models.ExamDetail;
import com.example.sunflower.models.SubmitResponse;
import com.example.sunflower.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakeExamActivity extends AppCompatActivity {

    private TextView tvTimer, tvQuestionCount, tvQuestionNumber, tvQuestionContent;
    private LinearLayout llAnswers;
    private RecyclerView rvPalette;
    private Button btnPrev, btnNext, btnSubmit;
    private ScrollView scrollView;
    private ImageView ivQuestionImage;  // ✅ Thêm ImageView cho ảnh
    private LinearLayout llImageContainer;  // ✅ Container cho ảnh

    private int examId;
    private String examName;
    private int duration;
    private List<CauHoi> questions;
    private Map<Integer, Integer> selectedAnswers;
    private int currentIndex = 0;
    private CountDownTimer timer;
    private long timeLeft;
    private PaletteAdapter paletteAdapter;
    private MediaPlayer mediaPlayer;
    private Handler audioHandler = new Handler();

    private Runnable autoNextRunnable;
    private Handler autoNextHandler = new Handler();
    private boolean isWaitingForAutoNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_exam);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Làm bài thi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        examId = getIntent().getIntExtra("exam_id", 1);
        examName = getIntent().getStringExtra("exam_name");
        duration = getIntent().getIntExtra("duration", 120);
        timeLeft = duration * 60 * 1000L;

        initViews();
        selectedAnswers = new HashMap<>();
        mediaPlayer = new MediaPlayer();

        String token = SharedPrefManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        RetrofitClient.setAuthToken(token);
        loadExam();
        startTimer();
    }

    private void initViews() {
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        llAnswers = findViewById(R.id.llAnswers);
        rvPalette = findViewById(R.id.rvPalette);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);
        scrollView = findViewById(R.id.scrollView);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        llImageContainer = findViewById(R.id.llImageContainer);

        btnPrev.setOnClickListener(v -> goPrev());
        btnNext.setOnClickListener(v -> goNext());
        btnSubmit.setOnClickListener(v -> confirmSubmit());
    }

    private void loadExam() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<ExamDetail> call = apiService.getExamDetail(examId);

        call.enqueue(new Callback<ExamDetail>() {
            @Override
            public void onResponse(Call<ExamDetail> call, Response<ExamDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions = response.body().getQuestions();

                    if (questions == null || questions.isEmpty()) {
                        Toast.makeText(TakeExamActivity.this, "Không có câu hỏi", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    tvQuestionCount.setText("0/" + questions.size());

                    paletteAdapter = new PaletteAdapter(questions, selectedAnswers, position -> {
                        currentIndex = position;
                        displayQuestion();
                        updatePalette();
                    });
                    rvPalette.setLayoutManager(new LinearLayoutManager(TakeExamActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvPalette.setAdapter(paletteAdapter);

                    displayQuestion();
                    updatePalette();
                } else {
                    Toast.makeText(TakeExamActivity.this, "Lỗi tải đề thi: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ExamDetail> call, Throwable t) {
                Toast.makeText(TakeExamActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updatePalette() {
        if (paletteAdapter != null) {
            paletteAdapter.setSelectedAnswers(selectedAnswers);
            paletteAdapter.setCurrentPosition(currentIndex);
        }
    }

    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) return;
        if (currentIndex >= questions.size()) return;

        CauHoi q = questions.get(currentIndex);
        if (q == null) return;

        cancelAutoNext();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        int answered = selectedAnswers.size();
        tvQuestionCount.setText(answered + "/" + questions.size());

        tvQuestionNumber.setText("Câu " + (currentIndex + 1) + " (Part " + q.getTenPart() + ")");

        String content = q.getNoiDung();
        if (content == null || content.isEmpty()) {
            content = "📢 Nghe audio hoặc đọc văn bản bên dưới";
        }
        tvQuestionContent.setText(content);

        // ✅ HIỂN THỊ ẢNH (CHO CẢ LISTENING VÀ READING)
        displayImage(q);

        // Hiển thị đáp án
        llAnswers.removeAllViews();
        List<DapAn> answers = q.getDap_an();
        if (answers != null && !answers.isEmpty()) {
            Integer selectedId = selectedAnswers.get(q.getMaCauHoi());
            for (DapAn ans : answers) {
                Button btnAnswer = new Button(this);
                btnAnswer.setText(ans.getKyHieu() + ". " + ans.getNoiDung());
                btnAnswer.setTag(ans.getMaDapAn());
                btnAnswer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnAnswer.getLayoutParams();
                params.setMargins(0, 0, 0, 16);
                btnAnswer.setLayoutParams(params);
                btnAnswer.setPadding(32, 24, 32, 24);
                btnAnswer.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL);
                btnAnswer.setTextSize(14);
                btnAnswer.setAllCaps(false);

                if (selectedId != null && selectedId == ans.getMaDapAn()) {
                    btnAnswer.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.sunflower_orange));
                    btnAnswer.setTextColor(getColor(android.R.color.white));
                } else {
                    btnAnswer.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_gray));
                    btnAnswer.setTextColor(getColor(android.R.color.black));
                }

                btnAnswer.setOnClickListener(v -> {
                    int answerId = (int) v.getTag();
                    selectedAnswers.put(q.getMaCauHoi(), answerId);
                    updatePalette();
                    displayQuestion();
                });

                llAnswers.addView(btnAnswer);
            }
        } else {
            TextView tvNoAnswer = new TextView(this);
            tvNoAnswer.setText("Không có đáp án");
            llAnswers.addView(tvNoAnswer);
        }

        btnPrev.setEnabled(currentIndex > 0);
        btnNext.setEnabled(currentIndex < questions.size() - 1);

        scrollView.fullScroll(ScrollView.FOCUS_UP);

        // Phát audio nếu có (chỉ cho Part 1-4)
        if (q.getTenPart() <= 4) {
            playAudioIfAvailable(q);
        }
    }

    // ✅ HÀM HIỂN THỊ ẢNH
    private void displayImage(CauHoi q) {
        String imageUrl = null;

        // Lấy ảnh từ câu hỏi
        if (q.getImgURL() != null && !q.getImgURL().isEmpty()) {
            imageUrl = q.getImgURL();
        }
        // Lấy ảnh từ nhóm (cho Part 3,4,6,7)
        else if (q.getNhom() != null && q.getNhom().getImages() != null
                && !q.getNhom().getImages().isEmpty()) {
            imageUrl = q.getNhom().getImages().get(0).getImgURL();
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            llImageContainer.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .into(ivQuestionImage);
        } else {
            llImageContainer.setVisibility(View.GONE);
            ivQuestionImage.setImageDrawable(null);
        }
    }

    private void playAudioIfAvailable(CauHoi q) {
        final String audioUrl;

        if (q.getNhom() != null && q.getNhom().getAudioURL() != null
                && !q.getNhom().getAudioURL().isEmpty()) {
            audioUrl = q.getNhom().getAudioURL();
        }
        else if (q.getAudioURL() != null && !q.getAudioURL().isEmpty()) {
            audioUrl = q.getAudioURL();
        } else {
            audioUrl = null;
        }

        if (audioUrl != null && !audioUrl.isEmpty()) {
            audioHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAudio(audioUrl);
                }
            }, 100);
        }
    }

    private void playAudio(String url) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (currentIndex < questions.size() && questions.get(currentIndex).getTenPart() <= 4) {
                        scheduleAutoNext();
                    }
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(TakeExamActivity.this, "Không thể phát audio", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi phát audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi phát audio, thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleAutoNext() {
        cancelAutoNext();
        isWaitingForAutoNext = true;

        Toast.makeText(this, "🎧 Audio kết thúc, tự động chuyển câu sau 3 giây...", Toast.LENGTH_SHORT).show();

        autoNextRunnable = new Runnable() {
            @Override
            public void run() {
                isWaitingForAutoNext = false;
                if (currentIndex < questions.size() - 1) {
                    goNext();
                }
            }
        };
        autoNextHandler.postDelayed(autoNextRunnable, 3000);
    }

    private void cancelAutoNext() {
        if (autoNextRunnable != null) {
            autoNextHandler.removeCallbacks(autoNextRunnable);
            autoNextRunnable = null;
        }
        isWaitingForAutoNext = false;
    }

    private void goPrev() {
        cancelAutoNext();
        if (currentIndex > 0) {
            currentIndex--;
            displayQuestion();
            updatePalette();
        }
    }

    private void goNext() {
        cancelAutoNext();
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            displayQuestion();
            updatePalette();
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                int hours = (int) (millisUntilFinished / 3600000);
                int minutes = (int) ((millisUntilFinished % 3600000) / 60000);
                int seconds = (int) ((millisUntilFinished % 60000) / 1000);

                String timeStr;
                if (hours > 0) {
                    timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                } else {
                    timeStr = String.format("%02d:%02d", minutes, seconds);
                }
                tvTimer.setText(timeStr);

                if (millisUntilFinished <= 300000) {
                    tvTimer.setTextColor(getColor(R.color.red));
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                submitExam();
            }
        }.start();
    }

    private void confirmSubmit() {
        int answered = selectedAnswers.size();
        int total = questions != null ? questions.size() : 0;

        new AlertDialog.Builder(this)
                .setTitle("Nộp bài")
                .setMessage("Bạn đã trả lời " + answered + "/" + total + " câu. Bạn có chắc muốn nộp bài?")
                .setPositiveButton("Nộp bài", (dialog, which) -> submitExam())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void submitExam() {
        if (timer != null) {
            timer.cancel();
        }

        cancelAutoNext();

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        int timeSpent = (int) ((duration * 60 * 1000L - timeLeft) / 1000);

        Map<String, Integer> answersMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : selectedAnswers.entrySet()) {
            answersMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        SubmitRequest request = new SubmitRequest(answersMap, new ArrayList<>(), timeSpent);

        ApiService apiService = RetrofitClient.getApiService();
        Call<SubmitResponse> call = apiService.submitExam(examId, request);

        call.enqueue(new Callback<SubmitResponse>() {
            @Override
            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SubmitResponse result = response.body();
                    Intent intent = new Intent(TakeExamActivity.this, ResultActivity.class);
                    intent.putExtra("diem", result.getTongDiem());
                    intent.putExtra("diem_lc", result.getDiemLC());
                    intent.putExtra("diem_rc", result.getDiemRC());
                    intent.putExtra("so_cau_dung", result.getSoCauDung());
                    intent.putExtra("so_cau_sai", result.getSoCauSai());
                    intent.putExtra("tong_cau", questions != null ? questions.size() : 0);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(TakeExamActivity.this, "Lỗi nộp bài", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubmitResponse> call, Throwable t) {
                Toast.makeText(TakeExamActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        cancelAutoNext();
        if (autoNextHandler != null) {
            autoNextHandler.removeCallbacksAndMessages(null);
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (audioHandler != null) {
            audioHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát bài thi")
                .setMessage("Bạn có chắc muốn thoát? Tiến trình sẽ không được lưu.")
                .setPositiveButton("Thoát", (dialog, which) -> {
                    if (timer != null) timer.cancel();
                    cancelAutoNext();
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    finish();
                })
                .setNegativeButton("Tiếp tục", null)
                .show();
        return true;
    }
}
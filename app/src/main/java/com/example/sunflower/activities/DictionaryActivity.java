package com.example.sunflower.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sunflower.R;
import com.example.sunflower.api.ApiService;
import com.example.sunflower.api.RetrofitClient;
import com.example.sunflower.models.DictionaryResponse;
import com.example.sunflower.utils.SharedPrefManager;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DictionaryActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private ProgressBar progressBar;
    private View layoutResult;
    private TextView tvWord, tvPhonetic, tvMeaning, tvExample;
    private ImageButton btnSpeak;

    private TextToSpeech textToSpeech;
    private String currentWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Khởi tạo TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        // Khôi phục token
        String token = SharedPrefManager.getInstance(this).getToken();
        if (token != null && !token.isEmpty()) {
            RetrofitClient.setAuthToken(token);
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
        layoutResult = findViewById(R.id.layoutResult);
        tvWord = findViewById(R.id.tvWord);
        tvPhonetic = findViewById(R.id.tvPhonetic);
        tvMeaning = findViewById(R.id.tvMeaning);
        tvExample = findViewById(R.id.tvExample);
        btnSpeak = findViewById(R.id.btnSpeak);
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> searchWord());
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWord();
                return true;
            }
            return false;
        });

        btnSpeak.setOnClickListener(v -> {
            if (!currentWord.isEmpty()) {
                speakWord(currentWord);
            }
        });
    }

    private void searchWord() {
        String word = etSearch.getText().toString().trim();

        if (TextUtils.isEmpty(word)) {
            Toast.makeText(this, "Vui lòng nhập từ cần tra", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        currentWord = word;

        ApiService apiService = RetrofitClient.getApiService();
        Call<DictionaryResponse> call = apiService.lookupWord(word);

        call.enqueue(new Callback<DictionaryResponse>() {
            @Override
            public void onResponse(Call<DictionaryResponse> call, Response<DictionaryResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null &&
                        "success".equals(response.body().getStatus())) {
                    displayResult(response.body().getData());
                } else {
                    tvWord.setText("Không tìm thấy từ \"" + word + "\"");
                    tvMeaning.setText("Vui lòng thử lại với từ khác");
                    tvExample.setText("");
                    tvPhonetic.setText("");
                    layoutResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<DictionaryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DictionaryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayResult(DictionaryResponse.DictionaryData data) {
        layoutResult.setVisibility(View.VISIBLE);
        currentWord = data.getWord();
        tvWord.setText(data.getWord());
        tvPhonetic.setText(data.getPhonetic() != null ? "/" + data.getPhonetic() + "/" : "");

        if (data.getMeanings() != null && !data.getMeanings().isEmpty()) {
            DictionaryResponse.Meaning meaning = data.getMeanings().get(0);
            if (meaning.getDefinitions() != null && !meaning.getDefinitions().isEmpty()) {
                DictionaryResponse.Definition def = meaning.getDefinitions().get(0);
                tvMeaning.setText(def.getDefinition());
                tvExample.setText(def.getExample() != null ? def.getExample() : "Chưa có ví dụ");
            } else {
                tvMeaning.setText("Không có định nghĩa");
                tvExample.setText("Chưa có ví dụ");
            }
        } else {
            tvMeaning.setText("Không có định nghĩa");
            tvExample.setText("Chưa có ví dụ");
        }

        // Nếu có audio URL từ API, phát trực tiếp
        if (data.getAudio() != null && !data.getAudio().isEmpty()) {
            playAudioFromUrl(data.getAudio());
        }
    }

    private void speakWord(String word) {
        if (textToSpeech != null) {
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void playAudioFromUrl(String audioUrl) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(mp -> mp.start());
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to TTS
            speakWord(currentWord);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
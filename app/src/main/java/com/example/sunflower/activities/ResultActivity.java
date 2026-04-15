package com.example.sunflower.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunflower.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int diem = getIntent().getIntExtra("diem", 0);
        int diemLC = getIntent().getIntExtra("diem_lc", 0);
        int diemRC = getIntent().getIntExtra("diem_rc", 0);
        int soCauDung = getIntent().getIntExtra("so_cau_dung", 0);
        int soCauSai = getIntent().getIntExtra("so_cau_sai", 0);
        int tongCau = getIntent().getIntExtra("tong_cau", 0);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvLC = findViewById(R.id.tvLC);
        TextView tvRC = findViewById(R.id.tvRC);
        TextView tvCorrect = findViewById(R.id.tvCorrect);
        TextView tvWrong = findViewById(R.id.tvWrong);
        Button btnDone = findViewById(R.id.btnDone);

        tvScore.setText(String.valueOf(diem));
        tvLC.setText("Listening: " + diemLC + "/495");
        tvRC.setText("Reading: " + diemRC + "/495");
        tvCorrect.setText("Đúng: " + soCauDung);
        tvWrong.setText("Sai: " + soCauSai + " / " + tongCau);

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
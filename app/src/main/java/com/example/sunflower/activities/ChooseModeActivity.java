package com.example.sunflower.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.sunflower.R;
import java.util.ArrayList;
import java.util.List;

public class ChooseModeActivity extends AppCompatActivity {

    private TextView tvExamName;
    private RadioGroup rgMode;
    private RadioButton rbFullTest, rbPracticePart;
    private LinearLayout llPartSelector;
    private LinearLayout llPartCheckboxes;
    private EditText etCustomTime;
    private Button btnStart;
    private Button btnCancel;

    private int examId;
    private String examName;
    private int defaultDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Lấy dữ liệu từ Intent
        examId = getIntent().getIntExtra("exam_id", 1);
        examName = getIntent().getStringExtra("exam_name");
        defaultDuration = getIntent().getIntExtra("duration", 120);

        if (examName == null) examName = "Test 1";

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvExamName = findViewById(R.id.tvExamName);
        rgMode = findViewById(R.id.rgMode);
        rbFullTest = findViewById(R.id.rbFullTest);
        rbPracticePart = findViewById(R.id.rbPracticePart);
        llPartSelector = findViewById(R.id.llPartSelector);
        llPartCheckboxes = findViewById(R.id.llPartCheckboxes);
        etCustomTime = findViewById(R.id.etCustomTime);
        btnStart = findViewById(R.id.btnStart);
        btnCancel = findViewById(R.id.btnCancel);

        tvExamName.setText(examName);

        // Tạo các checkbox cho Part 1-7
        createPartCheckboxes();
    }

    private void createPartCheckboxes() {
        if (llPartCheckboxes == null) return;

        llPartCheckboxes.removeAllViews();

        String[] partNames = {"Part 1", "Part 2", "Part 3", "Part 4", "Part 5", "Part 6", "Part 7"};
        int[] partNumbers = {1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < partNames.length; i++) {
            CheckBox cb = new CheckBox(this);
            cb.setText(partNames[i]);
            cb.setTag(partNumbers[i]);
            cb.setChecked(true);
            llPartCheckboxes.addView(cb);
        }
    }

    private void setupListeners() {
        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFullTest) {
                llPartSelector.setVisibility(View.GONE);
            } else {
                llPartSelector.setVisibility(View.VISIBLE);
            }
        });

        btnStart.setOnClickListener(v -> startExam());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void startExam() {
        boolean isFullTest = rbFullTest.isChecked();
        List<Integer> selectedParts = new ArrayList<>();
        int customMinutes;

        if (isFullTest) {
            customMinutes = defaultDuration;
            // selectedParts giữ nguyên là ArrayList rỗng
        } else {
            // Lấy các part được chọn
            if (llPartCheckboxes != null) {
                for (int i = 0; i < llPartCheckboxes.getChildCount(); i++) {
                    View child = llPartCheckboxes.getChildAt(i);
                    if (child instanceof CheckBox) {
                        CheckBox cb = (CheckBox) child;
                        if (cb.isChecked()) {
                            selectedParts.add((Integer) cb.getTag());
                        }
                    }
                }
            }

            if (selectedParts.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 Part!", Toast.LENGTH_SHORT).show();
                return;
            }

            String timeStr = etCustomTime.getText().toString().trim();
            if (timeStr.isEmpty()) {
                customMinutes = 30;
            } else {
                customMinutes = Integer.parseInt(timeStr);
            }
        }

        Intent intent = new Intent(ChooseModeActivity.this, TakeExamActivity.class);
        intent.putExtra("exam_id", examId);
        intent.putExtra("exam_name", examName);
        intent.putExtra("is_full_test", isFullTest);
        intent.putExtra("duration", customMinutes);
        // ✅ QUAN TRỌNG: Chỉ truyền nếu có dữ liệu
        if (!selectedParts.isEmpty()) {
            intent.putIntegerArrayListExtra("selected_parts", (ArrayList<Integer>) selectedParts);
        }
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
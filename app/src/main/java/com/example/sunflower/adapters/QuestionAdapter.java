// adapters/QuestionAdapter.java
package com.example.sunflower.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunflower.R;
import com.example.sunflower.models.CauHoi;
import com.example.sunflower.models.DapAn;
import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<CauHoi> questions;
    private Map<Integer, Integer> selectedAnswers;
    private OnAnswerSelectedListener listener;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int questionId, int answerId);
    }

    public QuestionAdapter(List<CauHoi> questions, OnAnswerSelectedListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    public void setSelectedAnswers(Map<Integer, Integer> selectedAnswers) {
        this.selectedAnswers = selectedAnswers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CauHoi question = questions.get(position);
        holder.tvNumber.setText("Câu " + question.getSTT());
        holder.tvContent.setText(question.getNoiDung() != null && !question.getNoiDung().isEmpty()
                ? question.getNoiDung() : "(Nghe audio hoặc xem hình)");

        // Clear previous radio group
        holder.radioGroup.removeAllViews();

        List<DapAn> answers = question.getDap_an();
        if (answers != null) {
            for (DapAn answer : answers) {
                RadioButton radioButton = new RadioButton(holder.itemView.getContext());
                radioButton.setText(answer.getKyHieu() + ". " + answer.getNoiDung());
                radioButton.setId(answer.getMaDapAn());
                radioButton.setPadding(16, 8, 16, 8);

                // Check if this answer was selected
                Integer selectedId = selectedAnswers != null ? selectedAnswers.get(question.getMaCauHoi()) : null;
                if (selectedId != null && selectedId == answer.getMaDapAn()) {
                    radioButton.setChecked(true);
                }

                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        listener.onAnswerSelected(question.getMaCauHoi(), answer.getMaDapAn());
                    }
                });

                holder.radioGroup.addView(radioButton);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvContent;
        RadioGroup radioGroup;

        ViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvContent = itemView.findViewById(R.id.tvContent);
            radioGroup = itemView.findViewById(R.id.radioGroup);
        }
    }
}
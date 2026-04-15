package com.example.sunflower.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunflower.R;
import com.example.sunflower.models.DeThi;
import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    private List<DeThi> examList;
    private OnExamClickListener listener;

    public interface OnExamClickListener {
        void onExamClick(DeThi exam);
    }

    public ExamAdapter(List<DeThi> examList, OnExamClickListener listener) {
        this.examList = examList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (examList == null || position >= examList.size()) return;

        DeThi exam = examList.get(position);
        if (exam == null) return;

        holder.tvTitle.setText(exam.getTenDeThi());
        holder.tvDescription.setText(exam.getMoTa());

        holder.tvInfo.setText(
                exam.getThoiGianLam() + " phút | " +
                        exam.getSoCau() + " câu"
        );

        if ("done".equals(exam.getTrangThai())) {
            holder.tvStatus.setText(exam.getTrangThaiText());
            holder.tvStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExamClick(exam);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList != null ? examList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvDescription, tvInfo, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
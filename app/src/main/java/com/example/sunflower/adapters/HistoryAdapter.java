package com.example.sunflower.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunflower.R;
import com.example.sunflower.models.HistorySession;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistorySession> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onHistoryClick(HistorySession session);
    }

    public HistoryAdapter(List<HistorySession> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistorySession session = historyList.get(position);

        holder.tvTitle.setText(session.getTenDeThi());
        holder.tvScore.setText(session.getDiemSo() + " điểm");
        holder.tvDetail.setText("LC: " + session.getDiemLC() + " | RC: " + session.getDiemRC());

        String date = session.getThoiGianKetThuc();
        if (date != null && date.length() > 10) {
            date = date.substring(0, 10);
        }
        holder.tvDate.setText(date);

        // Hiển thị số câu đúng/sai/bỏ qua
        holder.tvStats.setText("✓ " + session.getSoCauDung() + " đúng | ✗ " + session.getSoCauSai() + " sai | ○ " + session.getSoCauKhongChon() + " bỏ qua");

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoryClick(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle, tvScore, tvDetail, tvDate, tvStats;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStats = itemView.findViewById(R.id.tvStats);
        }
    }
}
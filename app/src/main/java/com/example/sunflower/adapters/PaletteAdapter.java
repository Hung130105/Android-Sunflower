package com.example.sunflower.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunflower.R;
import com.example.sunflower.models.CauHoi;

import java.util.List;
import java.util.Map;

public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.ViewHolder> {

    private List<CauHoi> questions;
    private Map<Integer, Integer> selectedAnswers;
    private int currentPosition;
    private OnPaletteClickListener listener;

    public interface OnPaletteClickListener {
        void onItemClick(int position);
    }

    public PaletteAdapter(List<CauHoi> questions, Map<Integer, Integer> selectedAnswers, OnPaletteClickListener listener) {
        this.questions = questions;
        this.selectedAnswers = selectedAnswers;
        this.listener = listener;
    }

    public void setSelectedAnswers(Map<Integer, Integer> selectedAnswers) {
        this.selectedAnswers = selectedAnswers;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_palette, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CauHoi q = questions.get(position);
        holder.tvNumber.setText(String.valueOf(position + 1));

        if (currentPosition == position) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.sunflower_orange));
            holder.tvNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
        } else if (selectedAnswers != null && selectedAnswers.containsKey(q.getMaCauHoi())) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.sunflower_green));
            holder.tvNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));
            holder.tvNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvNumber;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
        }
    }
}
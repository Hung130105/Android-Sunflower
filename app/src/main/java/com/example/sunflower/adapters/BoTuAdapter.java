package com.example.sunflower.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunflower.R;
import com.example.sunflower.models.BoTu;
import java.util.List;

public class BoTuAdapter extends RecyclerView.Adapter<BoTuAdapter.ViewHolder> {

    private List<BoTu> deckList;
    private OnDeckClickListener clickListener;
    private OnDeleteClickListener deleteListener;

    public interface OnDeckClickListener {
        void onDeckClick(BoTu deck);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(BoTu deck);
    }

    public BoTuAdapter(List<BoTu> deckList, OnDeckClickListener clickListener, OnDeleteClickListener deleteListener) {
        this.deckList = deckList;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoTu deck = deckList.get(position);

        holder.tvTitle.setText(deck.getTitle());
        holder.tvDescription.setText(deck.getDescription() != null ? deck.getDescription() : "Chưa có mô tả");
        holder.tvCount.setText(deck.getCard_count() + " thẻ");
        holder.tvIcon.setText(deck.getIcon() != null ? deck.getIcon() : "🌱");

        holder.cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDeckClick(deck);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(deck);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvIcon, tvTitle, tvDescription, tvCount;
        ImageView ivDelete;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCount = itemView.findViewById(R.id.tvCount);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
package com.javierviveros.entrevistacopilot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuestionMatchAdapter extends RecyclerView.Adapter<QuestionMatchAdapter.ViewHolder> {

    public interface OnQuestionClickListener {
        void onQuestionClick(GuideRepository.Question question);
    }

    private List<GuideRepository.Question> items = new ArrayList<>();
    private final OnQuestionClickListener listener;

    public QuestionMatchAdapter(OnQuestionClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<GuideRepository.Question> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_match, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuideRepository.Question q = items.get(position);

        holder.tvModuleBadge.setText("MOD " + q.moduleNum + " • P" + q.num);
        holder.tvTitle.setText(q.title);

        String preview = q.answer != null && !q.answer.isEmpty() ? q.answer : q.strategy;
        if (preview != null && preview.length() > 140) {
            preview = preview.substring(0, 137) + "...";
        }
        holder.tvPreview.setText(preview != null ? preview.replace("\n", " ") : "");

        if (q.matchedKeywords != null && !q.matchedKeywords.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String kw : q.matchedKeywords) {
                sb.append("[").append(kw).append("] ");
            }
            holder.tvKeywords.setText(sb.toString().trim());
            holder.tvKeywords.setVisibility(View.VISIBLE);
        } else {
            holder.tvKeywords.setVisibility(View.GONE);
        }

        if (q.matchScore > 0) {
            holder.tvScore.setText("★ " + q.matchScore);
            holder.tvScore.setVisibility(View.VISIBLE);
        } else {
            holder.tvScore.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionClick(q);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvModuleBadge;
        TextView tvKeywords;
        TextView tvScore;
        TextView tvTitle;
        TextView tvPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModuleBadge = itemView.findViewById(R.id.tvMatchModuleBadge);
            tvKeywords = itemView.findViewById(R.id.tvMatchKeywords);
            tvScore = itemView.findViewById(R.id.tvMatchScore);
            tvTitle = itemView.findViewById(R.id.tvMatchTitle);
            tvPreview = itemView.findViewById(R.id.tvMatchPreview);
        }
    }
}

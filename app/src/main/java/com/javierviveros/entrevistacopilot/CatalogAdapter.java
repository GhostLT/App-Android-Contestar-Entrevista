package com.javierviveros.entrevistacopilot;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private final Context context;
    private List<GuideRepository.Question> items = new ArrayList<>();
    private final Set<Integer> expandedPositions = new HashSet<>();

    public CatalogAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<GuideRepository.Question> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        expandedPositions.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pitch_qa, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GuideRepository.Question item = items.get(position);

        holder.tvNumber.setText("M" + item.moduleNum + "-P" + item.num);
        holder.tvTitle.setText(item.shortTitle);
        holder.tvQuestion.setText(item.title);

        String shortAns = item.strategy != null && !item.strategy.isEmpty() ?
                item.strategy : item.answer;
        if (shortAns != null && shortAns.length() > 220) {
            shortAns = shortAns.substring(0, 217) + "...";
        }
        holder.tvShortAnswer.setText(shortAns);

        StringBuilder sb = new StringBuilder();
        if (item.strategy != null && !item.strategy.isEmpty()) {
            sb.append("🎯 ESTRATEGIA:\n").append(item.strategy).append("\n\n");
        }
        if (item.answer != null && !item.answer.isEmpty()) {
            sb.append("⚡ RESPUESTA MODELO:\n").append(item.answer).append("\n\n");
        }
        if (item.extraSections != null) {
            for (GuideRepository.ExtraSection sec : item.extraSections) {
                sb.append("📌 ").append(sec.header).append(":\n").append(sec.body).append("\n\n");
            }
        }
        holder.tvFullAnswer.setText(sb.toString().trim());

        boolean isExpanded = expandedPositions.contains(position);
        holder.layoutFullAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.btnExpand.setRotation(isExpanded ? 180f : 0f);

        View.OnClickListener toggle = v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            if (expandedPositions.contains(pos)) {
                expandedPositions.remove(pos);
            } else {
                expandedPositions.add(pos);
            }
            notifyItemChanged(pos);
        };

        holder.itemView.setOnClickListener(toggle);
        holder.btnExpand.setOnClickListener(toggle);

        holder.itemView.setOnLongClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Q" + item.num, holder.tvFullAnswer.getText().toString());
            if (cm != null) {
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Respuesta copiada", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;
        TextView tvTitle;
        TextView tvQuestion;
        TextView tvShortAnswer;
        TextView tvFullAnswer;
        LinearLayout layoutFullAnswer;
        ImageButton btnExpand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvTitle = itemView.findViewById(R.id.tvQuestionTitle);
            tvQuestion = itemView.findViewById(R.id.tvQuestionText);
            tvShortAnswer = itemView.findViewById(R.id.tvShortAnswer);
            tvFullAnswer = itemView.findViewById(R.id.tvFullAnswer);
            layoutFullAnswer = itemView.findViewById(R.id.layoutFullAnswer);
            btnExpand = itemView.findViewById(R.id.btnExpandCollapse);
        }
    }
}

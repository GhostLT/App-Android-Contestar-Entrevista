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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PitchAdapter extends RecyclerView.Adapter<PitchAdapter.ViewHolder> {

    private final Context context;
    private final List<CandidateProfile.QuestionItem> items;
    private final Set<Integer> expandedPositions = new HashSet<>();

    public PitchAdapter(Context context, List<CandidateProfile.QuestionItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pitch_qa, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CandidateProfile.QuestionItem item = items.get(position);
        holder.tvNumber.setText("P" + item.id);
        holder.tvTitle.setText(item.title);
        holder.tvQuestion.setText(item.question);
        holder.tvShortAnswer.setText(item.shortAnswer);
        holder.tvFullAnswer.setText(item.fullAnswer);

        boolean isExpanded = expandedPositions.contains(position);
        holder.layoutFullAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.btnExpand.setRotation(isExpanded ? 180f : 0f);

        View.OnClickListener toggleListener = v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;
            if (expandedPositions.contains(currentPos)) {
                expandedPositions.remove(currentPos);
            } else {
                expandedPositions.add(currentPos);
            }
            notifyItemChanged(currentPos);
        };

        holder.itemView.setOnClickListener(toggleListener);
        holder.btnExpand.setOnClickListener(toggleListener);

        holder.itemView.setOnLongClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Respuesta P" + item.id, item.fullAnswer);
            if (cm != null) {
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Respuesta P" + item.id + " copiada", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
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

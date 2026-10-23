package com.javierviveros.entrevistacopilot;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final Context context;
    private List<DatabaseHelper.HistoryItem> items = new ArrayList<>();

    public HistoryAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<DatabaseHelper.HistoryItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseHelper.HistoryItem item = items.get(position);
        holder.tvTime.setText(item.timestamp);
        holder.tvLang.setText(item.language != null ? item.language.toUpperCase() : "ES");
        holder.tvQuestion.setText(item.question);
        holder.tvAnswer.setText(item.answer);

        holder.btnCopy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Respuesta Entrevista", item.answer);
            if (cm != null) {
                cm.setPrimaryClip(clip);
                Toast.makeText(context, "Respuesta copiada al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvLang;
        TextView tvQuestion;
        TextView tvAnswer;
        ImageButton btnCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvHistoryTime);
            tvLang = itemView.findViewById(R.id.tvHistoryLang);
            tvQuestion = itemView.findViewById(R.id.tvHistoryQuestion);
            tvAnswer = itemView.findViewById(R.id.tvHistoryAnswer);
            btnCopy = itemView.findViewById(R.id.btnCopyHistory);
        }
    }
}

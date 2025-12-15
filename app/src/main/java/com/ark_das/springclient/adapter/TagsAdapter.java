package com.ark_das.springclient.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.model.TagItem;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private List<TagItem> tags;
    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(TagItem tag);
    }

    public TagsAdapter(List<TagItem> tags, OnTagClickListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TagItem tag = tags.get(position);

        holder.textView.setText(tag.getName());

        // Подсветка
        if (tag.isSelected()) {
            holder.textView.setBackgroundResource(R.drawable.tag_selected_bg);
            holder.textView.setTextColor(Color.WHITE);
        } else {
            holder.textView.setBackgroundResource(R.drawable.tag_default_bg);
            holder.textView.setTextColor(Color.parseColor("#4A148C"));
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onTagClick(tag);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tag_text);
        }
    }
}

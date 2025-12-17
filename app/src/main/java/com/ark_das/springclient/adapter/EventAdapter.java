package com.ark_das.springclient.adapter;

import static android.view.View.GONE;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.model.Event;

import com.ark_das.springclient.model.Tag;


import java.util.ArrayList;

import java.util.List;


public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

    private List<Event> eventList;
    //private List<Tag> tagList;
    private List<Event> eventListFull; // для поиска
    private Event event;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList != null ? eventList : new ArrayList<>();
        //this.tagList = tagList != null ? tagList : new ArrayList<>();
        this.eventListFull = new ArrayList<>(this.eventList);
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_event_items, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        // Защита от null и выхода за границы
        if (eventList == null || eventList.isEmpty() || position < 0 || position >= eventList.size()) {
            bindEmptyView(holder);
            return;
        }
        event = eventList.get(position);
        if (event == null) {
            bindEmptyView(holder);
            return;
        }
        holder.id.setText(String.valueOf(event.getId()));
        holder.title.setText(event.getTitle());
        bindTags(holder);
        holder.create.setText(holder.itemView.getContext().getString(R.string.event_create_by) + ": ");
        holder.create_by.setText(String.valueOf(event.getCreate_by()));
    }

    private void bindEmptyView(EventHolder holder) {
        holder.id.setText("");
        holder.title.setText("");
        holder.create_by.setText("");
        bindTags(holder);
    }


    private void bindTags(EventHolder holder) {
        List<Tag> listTags = new ArrayList<>(event.getTags());

        for (int i = 0; i < holder.tags.length; i++) {
            TextView view = holder.tags[i];

            if (i < listTags.size()) {
                Tag tag = listTags.get(i);
                if (tag != null) {
                    view.setVisibility(View.VISIBLE);
                    view.setText(tag.getName());
                } else {
                    view.setVisibility(View.GONE);
                }
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateData(List<Event> newEventList, List<Tag> newTagList) {
        this.eventList = newEventList != null ? newEventList : new ArrayList<>();
        //this.tagList = newTagList != null ? newTagList : new ArrayList<>();
        this.eventListFull = new ArrayList<>(this.eventList);
        notifyDataSetChanged();
    }

    /*


    // Метод для обновления данных


    // Метод только для пользователей
    public void setEvents(List<Event> evntList) {
        this.eventList = eventList != null ? eventList : new ArrayList<>();
        this.eventListFull = new ArrayList<>(this.eventList);
        notifyDataSetChanged();
    }

    // Метод только для ролей
    public void setTags(List<Tag> tagList) {
        this.tagList = tagList != null ? tagList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Опционально: добавьте поиск/фильтрацию
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Event event : eventListFull) {
                    if (event.getTitle() != null && event.getTitle().toLowerCase().contains(filterPattern) ||
                            event.getLast_name() != null && user.getLast_name().toLowerCase().contains(filterPattern) ||
                            user.getEmail() != null && user.getEmail().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };*/

}
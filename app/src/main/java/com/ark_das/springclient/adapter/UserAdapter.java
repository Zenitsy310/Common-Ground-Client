package com.ark_das.springclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserHolder>{

    private List<User> userList;

    public UserAdapter(List<User> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user_items, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = userList.get(position);
        holder.login.setText(user.getLogin());
        holder.email.setText(user.getEmail());
        holder.created_at.setText(user.getCreated_at().toString());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

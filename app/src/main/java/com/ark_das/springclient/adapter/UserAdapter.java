package com.ark_das.springclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.model.Role;
import com.ark_das.springclient.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserHolder>{

    private final List<User> userList;
    private final List<Role> roleList;


    public UserAdapter(List<User> userList, List<Role> roleList){
        this.userList = userList;
        this.roleList = roleList;
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
        holder.first_name.setText(user.getFirst_name());
        holder.last_name.setText(user.getLast_name());
        holder.email.setText(user.getEmail());
        holder.role.setText(String.valueOf(roleList.get(user.getRole_id()).getName()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

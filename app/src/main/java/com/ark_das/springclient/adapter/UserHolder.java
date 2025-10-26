package com.ark_das.springclient.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;

public class UserHolder extends RecyclerView.ViewHolder{

    TextView login, email, created_at;
    public UserHolder(@NonNull View itemView) {
        super(itemView);
        login = itemView.findViewById(R.id.userListItem_login);
        email = itemView.findViewById(R.id.userListItem_email);
        created_at = itemView.findViewById(R.id.userListItem_created_at);
    }
}

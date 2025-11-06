package com.ark_das.springclient.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;

public class UserHolder extends RecyclerView.ViewHolder{

    TextView first_name, last_name, email, role;
    public UserHolder(@NonNull View itemView) {
        super(itemView);
        first_name = itemView.findViewById(R.id.user_first_name);
        last_name = itemView.findViewById(R.id.user_last_name);
        email = itemView.findViewById(R.id.user_email);
        role = itemView.findViewById(R.id.user_role);
    }
}

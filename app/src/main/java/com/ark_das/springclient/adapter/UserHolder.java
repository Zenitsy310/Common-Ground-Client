package com.ark_das.springclient.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.LoginForm;
import com.ark_das.springclient.R;
import com.ark_das.springclient.UserForm;
import com.ark_das.springclient.UserListActivity;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserHolder extends RecyclerView.ViewHolder{

    TextView first_name, last_name, email, role, id;

    ImageButton user_action;
    public UserHolder(@NonNull View itemView) {
        super(itemView);
        first_name = itemView.findViewById(R.id.user_first_name);
        last_name = itemView.findViewById(R.id.user_last_name);
        email = itemView.findViewById(R.id.user_email);
        role = itemView.findViewById(R.id.user_role);
        user_action = itemView.findViewById(R.id.user_action);
        id = itemView.findViewById(R.id.user_id);
        user_action.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), UserForm.class);
            //intent.putExtra("userCurrent", );
            itemView.getContext().startActivity(intent);
        });

    }

}

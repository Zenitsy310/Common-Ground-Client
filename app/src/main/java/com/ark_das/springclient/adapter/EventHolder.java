package com.ark_das.springclient.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.ui.EventFormActivity;
import com.ark_das.springclient.ui.UserForm;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EventHolder extends RecyclerView.ViewHolder{

    TextView title, create_by, id;
    TextView[] tags;

    ImageButton event_action;
    public EventHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        tags[0] = itemView.findViewById(R.id.tag_1);
        tags[1] = itemView.findViewById(R.id.tag_2);
        tags[2] = itemView.findViewById(R.id.tag_3);
        create_by = itemView.findViewById(R.id.event_create_by);
        event_action = itemView.findViewById(R.id.event_action);
        id = itemView.findViewById(R.id.event_id);
        event_action.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), EventFormActivity.class);
            String idText = id.getText().toString(); // "123"
            int userId = Integer.parseInt(idText);
            intent.putExtra("eventId", userId);
            Logger.getLogger(UserForm.class.getName()).log(Level.INFO, (String) id.getText());
            intent.putExtra("mode", "update");
            itemView.getContext().startActivity(intent);
        });

    }

}

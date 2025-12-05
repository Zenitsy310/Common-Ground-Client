package com.ark_das.springclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ark_das.springclient.R;
import com.ark_das.springclient.adapter.EventAdapter;
import com.ark_das.springclient.adapter.UserAdapter;
import com.ark_das.springclient.model.Event;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.EventApi;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivity extends AppCompatActivity {

    //private List<Event> events;
    private TextView emptyState;
    EventAdapter eventAdapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_event_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Вынести в отдельную функции для инициализации компонентов
        initialViews();
    }

    private void initialViews() {
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
            floatingActionButton =findViewById(R.id.event_list_fab);
            BottomMenuView bottomMenuView = findViewById(R.id.bottomMenuView);
            bottomMenuView.setActive(R.id.nav_event);
            bottomMenuView.setOnItemSelectedListener(id -> {
                if (id == R.id.nav_user) {
                    startActivity(new Intent(this, UserListActivity.class));
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, UserListActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, UserListActivity.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(this, UserListActivity.class));
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshEventList();
                }
            });
        floatingActionButton.setOnClickListener( v ->{
            Intent intent = new Intent(this, EventFormActivity.class);
            //intent.putExtra("mode", "create");
            startActivity(intent);
        });


        recyclerView = findViewById(R.id.event_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyState = findViewById(R.id.emptyState_textView);
        progressBar = findViewById(R.id.progressBar);
        eventAdapter = new EventAdapter(new ArrayList<>());
        recyclerView.setAdapter(eventAdapter);
        loadEvents();

    }

    private void refreshEventList() {
        swipeRefreshLayout.setRefreshing(true);

        // Здесь твой код загрузки пользователей (например, из API или базы)
        // Для примера через Handler с задержкой 2 сек:
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadEvents();
                // Скрываем индикатор
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
    private void loadEvents(){
        RetrofitService retrofit = new RetrofitService();
        EventApi api = retrofit.getRetrofit().create(EventApi.class);

        api.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful() && response.body() != null){
                    eventAdapter = new EventAdapter(response.body());
                    recyclerView.setAdapter(eventAdapter);
                    Logger.getLogger(EventListActivity.class.getName()).log(Level.INFO,
                            "Events loaded: +" + response.body().size());
                    checkEmptyState(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                System.out.println(t);
            }
        });


    }
    private void checkEmptyState(List<Event> events) {
        if (events == null || events.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }
    }
    private void showEmptyState(boolean show) {
        if (show) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}

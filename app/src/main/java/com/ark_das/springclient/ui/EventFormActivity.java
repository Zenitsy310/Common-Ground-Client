package com.ark_das.springclient.ui;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.adapter.EventAdapter;
import com.ark_das.springclient.adapter.TagsAdapter;
import com.ark_das.springclient.dto.UserResponse;
import com.ark_das.springclient.model.Event;
import com.ark_das.springclient.model.Tag;
import com.ark_das.springclient.model.TagItem;
import com.ark_das.springclient.retrofit.EventApi;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.TagApi;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFormActivity extends AppCompatActivity {
    TextView currentDateTime;
    Calendar dateAndTime=Calendar.getInstance();
    ImageButton btn_menu;

    TextInputLayout layout_title,layout_description,layout_address
            , layout_datetime;

    Button btn_create,btn_cancel_create,btn_delete;
    final private int max_tags = 3;

    RecyclerView recyclerTags;
    LinearLayout selectedContainer;
    TagsAdapter adapter;

    List<TagItem> tags = new ArrayList<>();
    List<TagItem> selectedTags = new ArrayList<>();

    @NotEmpty(message = "Введите название мероприятия")
    @Length(min = 2, message = "Должно быть не меньше 2 символов")
    TextInputEditText input_title;

    @NotEmpty(message = "Введите описание мероприятия")
    @Length(min = 2, message = "Должно быть не меньше 2 символов")
    TextInputEditText input_description;

    @NotEmpty(message = "Введите адресс")
    //@Pattern(regex = ".*[@].*", message = "Укажите вашу полную почту, включая символ @")
    @Length(min = 3, message = "Должно быть не короче 3 символов")
    TextInputEditText input_address;

    @NotEmpty(message = "")
    @Length(min = 3, message = "Должно быть не короче 3 символов")
    TextInputEditText input_datetime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_event_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currentDateTime = findViewById(R.id.input_datetime);
        initializeComponents();
        setInitialDateTime();
    }

    private void initializeComponents() {
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(view -> {
            onBackPressed();
        });
        layout_title = findViewById(R.id.layout_title);
        layout_description = findViewById(R.id.layout_description);
        layout_datetime = findViewById(R.id.layout_datetime);
        layout_address = findViewById(R.id.layout_address);
        recyclerTags = findViewById(R.id.recycler_tags);
        selectedContainer = findViewById(R.id.selected_tags_container);

        initTags();
        setupRecycler();
        setupTagSelector();
    }
    private void initTags() {
        /*tags.add(new TagItem("Музыка"));
        tags.add(new TagItem("Спорт"));
        tags.add(new TagItem("IT"));
        tags.add(new TagItem("Образование"));
        tags.add(new TagItem("Вечеринка"));
        tags.add(new TagItem("Кино"));*/
        RetrofitService retrofit = new RetrofitService();
        TagApi api = retrofit.getRetrofit().create(TagApi.class);

        api.getAllTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for(Tag t:response.body()){
                        tags.add(new TagItem(t));
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                System.out.println(t);
            }
        });

    }
    private void setupRecycler() {
        adapter = new TagsAdapter(tags, tag -> toggleTag(tag));
        recyclerTags.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerTags.setAdapter(adapter);
    }

    private void setupTagSelector() {
        LinearLayout selector = findViewById(R.id.tags_selector);

        selector.setOnClickListener(v -> {
            if (recyclerTags.getVisibility() == View.GONE) {
                recyclerTags.setVisibility(View.VISIBLE);
            } else {
                recyclerTags.setVisibility(View.GONE);
            }
        });
    }
    private void toggleTag(TagItem tag) {
        // Если хотим включить тег, но уже n выбрано
        if (!tag.isSelected() && selectedTags.size() >= max_tags) {
            Toast.makeText(this, "Можно выбрать не больше " + max_tags + " тэгов", Toast.LENGTH_SHORT).show();
            return;
        }
        // Переключаем состояние
        tag.setSelected(!tag.isSelected());

        if (tag.isSelected()) {
            selectedTags.add(tag);
            addTagChip(tag);
        } else {
            selectedTags.remove(tag);
            removeTagChip(tag);
        }
    }


    private void addTagChip(TagItem tag) {
        TextView chip = new TextView(this);
        chip.setText(tag.getName());
        chip.setPadding(20, 12, 20, 12);
        chip.setTextColor(Color.WHITE);
        chip.setBackgroundResource(R.drawable.tag_selected_bg);
        chip.setTextSize(15);

        chip.setTag(tag.getName());
        chip.setOnClickListener(v -> {
            tag.setSelected(false);
            selectedTags.remove(tag);
            removeTagChip(tag);
            adapter.notifyDataSetChanged();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);
        chip.setLayoutParams(params);

        selectedContainer.addView(chip);
    }

    private void removeTagChip(TagItem tag) {
        for (int i = 0; i < selectedContainer.getChildCount(); i++) {
            View view = selectedContainer.getChildAt(i);
            if (view.getTag().equals(tag.getName())) {
                selectedContainer.removeView(view);
                break;
            }
        }
    }


    public void setDate(View v) {
        new DatePickerDialog(EventFormActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(EventFormActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }
    // установка начальных даты и времени
    private void setInitialDateTime() {
        int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
        int month = dateAndTime.get(Calendar.MONTH) + 1;
        int year = dateAndTime.get(Calendar.YEAR);
        int hour = dateAndTime.get(Calendar.HOUR_OF_DAY);
        int minute = dateAndTime.get(Calendar.MINUTE);

        String formatted = String.format(Locale.getDefault(),
                "%04d-%02d-%02d %02d:%02d",
                year, month, day, hour, minute);

        currentDateTime.setText(formatted);

    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };
    public void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выполнить?").setMessage("Вы действительно хотите выполнить это действие?").setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteEvent();
            }
        }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Действие при отмене
                dialog.dismiss(); // Закрываем диалог
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void deleteEvent(){
        /*RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        userApi.deleteById(userId)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if(response.isSuccessful() && response.body().isSuccess()){
                            Toast.makeText(UserForm.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            clearForm();
                        }else{
                            Toast.makeText(UserForm.this,
                                    response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(UserForm.this, "Server eror", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, "Error occured", t);
                    }
                });*/
    }
}


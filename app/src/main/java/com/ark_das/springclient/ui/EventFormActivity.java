package com.ark_das.springclient.ui;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.R;
import com.ark_das.springclient.dto.UserResponse;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFormActivity extends AppCompatActivity {
    TextView currentDateTime;
    Calendar dateAndTime=Calendar.getInstance();

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

        setInitialDateTime();
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


        //System.out.println(LocalDateTime.of());
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


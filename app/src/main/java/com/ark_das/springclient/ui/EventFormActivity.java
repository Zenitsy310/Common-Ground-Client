package com.ark_das.springclient.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.ark_das.springclient.adapter.TagsAdapter;
import com.ark_das.springclient.dto.EventRequest;
import com.ark_das.springclient.dto.EventResponse;
import com.ark_das.springclient.model.Event;
import com.ark_das.springclient.model.Tag;
import com.ark_das.springclient.model.TagItem;
import com.ark_das.springclient.retrofit.EventApi;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.TagApi;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFormActivity extends AppCompatActivity implements Validator.ValidationListener {

    private static final Logger LOGGER = Logger.getLogger(EventFormActivity.class.getName());

    // Параметры из Intent и режим
    private Bundle arguments;
    private int eventId;
    private String mode; // "create" или "update"
    private static final DateTimeFormatter UI_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    // --- Время и Дата ---
    TextView currentDateTime; // Используется как TextView в XML, но для отображения/получения даты
    Calendar dateAndTime = Calendar.getInstance();

    // --- Saripaar Validator ---
    private Validator validator;

    // --- Layouts и Поля ввода (для Saripaar и ошибок) ---
    TextInputLayout layout_title, layout_description, layout_address, layout_datetime;

    @NotEmpty(message = "Введите название мероприятия")
    @Length(min = 2, message = "Должно быть не меньше 2 символов")
    TextInputEditText input_title;

    @NotEmpty(message = "Введите описание мероприятия")
    @Length(min = 2, message = "Должно быть не меньше 2 символов")
    TextInputEditText input_description;

    @NotEmpty(message = "Введите адресс")
    @Length(min = 3, message = "Должно быть не короче 3 символов")
    TextInputEditText input_address;

    @NotEmpty(message = "Укажите дату и время")
    // NOTE: input_datetime - это поле, которое заполняется автоматически
    TextInputEditText input_datetime;


    // --- Кнопки и Контейнеры ---
    ImageButton btn_menu;
    Button btn_create, btn_cancel_create, btn_delete;
    LinearLayout create_buttons_layout, edit_buttons_layout; // Контейнеры для управления видимостью

    // --- Теги ---
    final private int max_tags = 3;
    RecyclerView recyclerTags;
    LinearLayout selectedContainer;
    TagsAdapter adapter;
    List<TagItem> tags = new ArrayList<>();
    List<TagItem> selectedTags = new ArrayList<>();


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

        Intent intent = getIntent();
        if (intent != null) {
            arguments = intent.getExtras();
            if (arguments != null) {
                eventId = arguments.getInt("eventId");
                mode = arguments.getString("mode");
                LOGGER.log(Level.INFO, "Received arguments - eventId: " + eventId + ", mode: " + mode);
            }
        }

        currentDateTime = findViewById(R.id.input_datetime); // используется для setInitialDateTime
        initializeComponents();
        setInitialDateTime();

        // --- Логика инициализации режима ---
        if (arguments != null && arguments.getString("mode") != null && arguments.getString("mode").equals("update")) {
            setupEditMode();
        } else {
            setupCreateMode();
        }
    }

    private void initializeComponents() {
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(view -> onBackPressed());

        // 1. Инициализация Layouts и Полей ввода
        layout_title = findViewById(R.id.layout_title);
        layout_description = findViewById(R.id.layout_description);
        layout_datetime = findViewById(R.id.layout_datetime);
        layout_address = findViewById(R.id.layout_address);

        input_title = findViewById(R.id.input_title);
        input_description = findViewById(R.id.input_description);
        input_address = findViewById(R.id.input_address);
        input_datetime = findViewById(R.id.input_datetime); // Это же поле используется для date/time picker

        // 2. Инициализация кнопок и контейнеров
        btn_create = findViewById(R.id.btn_create);
        btn_cancel_create = findViewById(R.id.btn_cancel_create);
        btn_delete = findViewById(R.id.btn_delete);

        create_buttons_layout = findViewById(R.id.create_buttons);
        edit_buttons_layout = findViewById(R.id.edit_buttons);

        // 3. Инициализация Saripaar Validator
        validator = new Validator(this);
        validator.setValidationListener(this);

        // 4. Обработчики кликов
        btn_create.setOnClickListener(view -> validator.validate());
        btn_cancel_create.setOnClickListener(view -> clearForm());
        btn_delete.setOnClickListener(view -> confirmDelete());

        // 5. Инициализация тегов
        recyclerTags = findViewById(R.id.recycler_tags);
        selectedContainer = findViewById(R.id.selected_tags_container);
        initTags();
        setupRecycler();
        setupTagSelector();
    }

    // --- Логика режимов ---

    private void setupEditMode(){
        // Используем кнопки из секции создания, но меняем текст и включаем удаление
        create_buttons_layout.setVisibility(View.VISIBLE);
        edit_buttons_layout.setVisibility(View.GONE);
        btn_delete.setVisibility(View.VISIBLE);

        btn_create.setText(R.string.save); // Меняем текст кнопки на "Сохранить"

        setEventInfoById();
    }

    private void setupCreateMode(){
        create_buttons_layout.setVisibility(View.VISIBLE);
        edit_buttons_layout.setVisibility(View.GONE);
        btn_delete.setVisibility(View.GONE);
        btn_create.setText(R.string.btn_create); // Убеждаемся, что текст "Создать"
    }

    // --- Загрузка и Заполнение данных ---

    private void fillForm(Event event) {
        input_title.setText(event.getTitle());
        input_description.setText(event.getDescription());
        input_address.setText(event.getPlace());
        if (event.getTime() != null) {
            LocalDateTime loadedDateTime = event.getTime();

            // 1. Форматируем LocalDateTime в строку для отображения в поле
            String formattedDateTime = loadedDateTime.format(UI_FORMATTER);
            input_datetime.setText(formattedDateTime);

            // 2. Обновляем Calendar для Date/Time Picker
            dateAndTime.set(Calendar.YEAR, loadedDateTime.getYear());
            dateAndTime.set(Calendar.MONTH, loadedDateTime.getMonthValue() - 1); // Месяц в Calendar с 0
            dateAndTime.set(Calendar.DAY_OF_MONTH, loadedDateTime.getDayOfMonth());
            dateAndTime.set(Calendar.HOUR_OF_DAY, loadedDateTime.getHour());
            dateAndTime.set(Calendar.MINUTE, loadedDateTime.getMinute());

        }

        fillEventTags(event.getTags());
    }

    private void fillEventTags(Set<Tag> eventTags) {
        // 1. Очищаем предыдущее состояние
        selectedContainer.removeAllViews();
        selectedTags.clear();

        // 2. Сбрасываем состояние isSelected для ВСЕХ элементов в списке адаптера (tags)
        for(TagItem availableTagItem : tags) {
            availableTagItem.setSelected(false);
        }

        // 3. Проходим по тегам, которые пришли с сервера (eventTags)
        for(Tag eventTag : eventTags){

            // 4. Ищем соответствующий TagItem в основном списке тегов (this.tags)
            for(TagItem availableTagItem : tags) {

                // Сравнение по ID - это надежный способ!
                if(eventTag.getId() == availableTagItem.getId()) {

                    availableTagItem.setSelected(true);

                    selectedTags.add(availableTagItem);
                    addTagChip(availableTagItem);
                    break;
                }
            }
        }

        // 7. Уведомляем адаптер RecyclerView, чтобы он перерисовал элементы
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setEventInfoById() {
        RetrofitService retrofitService = new RetrofitService();
        EventApi eventApi = retrofitService.getRetrofit().create(EventApi.class);

        eventApi.getEventById(eventId).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getEvent() != null) {
                    fillForm(response.body().getEvent());
                    LOGGER.log(Level.INFO, "Event data loaded and form filled");
                } else {
                    Toast.makeText(EventFormActivity.this, "Failed to load event data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(EventFormActivity.this, "Failed to found event", Toast.LENGTH_SHORT).show();
                LOGGER.log(Level.SEVERE, "Error occurred", t);
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        clearAllErrors();

        String title = input_title.getText().toString().trim();
        String description = input_description.getText().toString().trim();
        String address = input_address.getText().toString().trim();
        String datetimeString = input_datetime.getText().toString();

        // 1. Преобразование строки даты/времени в LocalDateTime
        LocalDateTime time;
        try {
            // Формат, который мы используем: "yyyy-MM-dd HH:mm"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            time = LocalDateTime.parse(datetimeString, formatter);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to parse datetime string: " + datetimeString, e);
            Toast.makeText(EventFormActivity.this, "Ошибка формата даты/времени.", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Сбор названий тегов (Set<String>)
        Set<String> tagNames = new HashSet<>();
        for (TagItem tagItem : selectedTags) {
            tagNames.add(tagItem.getName());
        }


        int userId = 1; // <--- ЗАМЕНИТЬ НА РЕАЛЬНЫЙ ID ПОЛЬЗОВАТЕЛЯ

        // 4. Создаем EventRequest (напрямую, используя DTO)
        EventRequest eventRequest = new EventRequest(
                title,
                description,
                address, // В DTO это 'place', но мы используем данные из поля 'address'
                time,
                userId,
                tagNames
        );

        // 5. Вызываем функцию сохранения/обновления
        saveOrUpdateEvent(eventRequest);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        clearAllErrors();

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Установка ошибок для TextInputLayout
            if (view.getId() == R.id.input_title) {
                layout_title.setError(message);
            } else if (view.getId() == R.id.input_description) {
                layout_description.setError(message);
            } else if (view.getId() == R.id.input_address) {
                layout_address.setError(message);
            } else if (view.getId() == R.id.input_datetime) {
                layout_datetime.setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    // --- Сохранение и Удаление ---
    private void saveOrUpdateEvent(EventRequest eventRequest){
        RetrofitService retrofitService = new RetrofitService();
        EventApi eventApi = retrofitService.getRetrofit().create(EventApi.class);
        System.out.println(mode);
        if(mode.equals("create")) {
            // --- РЕЖИМ СОЗДАНИЯ (POST) ---

            eventApi.saveEventWithTags(eventRequest)
                    .enqueue(new Callback<EventResponse>() {
                        @Override
                        public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(EventFormActivity.this, "Событие успешно создано!", Toast.LENGTH_SHORT).show();
                                clearForm();
                            } else {
                                String errorMsg = response.errorBody() != null ? response.message() : "Неизвестная ошибка";
                                Toast.makeText(EventFormActivity.this,
                                        "Ошибка создания. Код: " + response.code() + ". " + errorMsg, Toast.LENGTH_LONG).show();
                                LOGGER.log(Level.WARNING, "Creation failed. HTTP: " + response.code() + ", Error: " + errorMsg);
                            }
                        }
                        @Override
                        public void onFailure(Call<EventResponse> call, Throwable t) {
                            Toast.makeText(EventFormActivity.this, "Ошибка сервера или сети.", Toast.LENGTH_SHORT).show();
                            LOGGER.log(Level.SEVERE, "Error occurred during CREATE", t);
                        }
                    });
        } else if (mode.equals("update")) {
            System.out.print(eventId);
            eventRequest.setId(eventId);
            eventApi.saveEventWithTags(eventRequest)
                    .enqueue(new Callback<EventResponse>() {
                        @Override
                        public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(EventFormActivity.this, "Событие успешно обновлено!" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = response.errorBody() != null ? response.message() : "Неизвестная ошибка";
                                Toast.makeText(EventFormActivity.this,
                                        "Ошибка обновления. Код: " + response.code() + ". " + errorMsg, Toast.LENGTH_LONG).show();
                                LOGGER.log(Level.WARNING, "Update failed. HTTP: " + response.code() + ", Error: " + errorMsg);
                            }
                        }

                        @Override
                        public void onFailure(Call<EventResponse> call, Throwable t) {
                            Toast.makeText(EventFormActivity.this, "Ошибка сервера или сети.", Toast.LENGTH_SHORT).show();
                            LOGGER.log(Level.SEVERE, "Error occurred during UPDATE", t);
                        }
                    });
        }
    }

    private void deleteEvent() {
        RetrofitService retrofitService = new RetrofitService();
        EventApi eventApi = retrofitService.getRetrofit().create(EventApi.class);

        eventApi.deleteEventById(eventId)
                .enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EventFormActivity.this, "Событие успешно удалено!", Toast.LENGTH_SHORT).show();
                            finish(); // Закрываем Activity после удаления
                        } else {
                            Toast.makeText(EventFormActivity.this, "Ошибка удаления: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                        Toast.makeText(EventFormActivity.this, "Ошибка сервера", Toast.LENGTH_SHORT).show();
                        LOGGER.log(Level.SEVERE, "Произошла ошибка при вызове EventApi", t);
                    }
                });
    }

    public void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить событие?").setMessage("Вы действительно хотите удалить это событие?").setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteEvent();
            }
        }).setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // --- Управление формой ---

    private void clearAllErrors() {
        layout_title.setError(null);
        layout_description.setError(null);
        layout_address.setError(null);
        layout_datetime.setError(null);
    }

    private void clearFields(){
        input_title.setText("");
        input_description.setText("");
        input_address.setText("");

        // Сброс тегов
        selectedTags.clear();
        selectedContainer.removeAllViews();
        if (adapter != null) {
            // Предполагая, что в TagsAdapter есть метод для сброса выбора
            // adapter.resetSelection();
            adapter.notifyDataSetChanged();
        }
        setInitialDateTime();
    }

    private void clearForm(){
        clearAllErrors();
        clearFields();
    }

    // --- Логика тегов (как было в исходном коде) ---

    private void initTags() {
        RetrofitService retrofit = new RetrofitService();
        TagApi api = retrofit.getRetrofit().create(TagApi.class);

        api.getAllTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if(response.isSuccessful() && response.body() != null){
                    tags.clear();
                    for(Tag t:response.body()){
                        tags.add(new TagItem(t));
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                LOGGER.log(Level.SEVERE, "Error loading tags", t);
                Toast.makeText(EventFormActivity.this, "Ошибка загрузки тегов", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecycler() {
        adapter = new TagsAdapter(tags, this::toggleTag);
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
        if (!tag.isSelected() && selectedTags.size() >= max_tags) {
            Toast.makeText(this, "Можно выбрать не больше " + max_tags + " тэгов", Toast.LENGTH_SHORT).show();
            return;
        }

        tag.setSelected(!tag.isSelected());

        if (tag.isSelected()) {
            selectedTags.add(tag);
            addTagChip(tag);
        } else {
            selectedTags.remove(tag);
            removeTagChip(tag);
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void addTagChip(TagItem tag) {
        // (Оставлен оригинальный код добавления Chip)
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
            if (adapter != null) adapter.notifyDataSetChanged();
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
            if (view.getTag() != null && view.getTag().equals(tag.getName())) {
                selectedContainer.removeView(view);
                break;
            }
        }
    }


    // --- Логика выбора даты и времени (как было в исходном коде) ---

    public void setDate(View v) {
        new DatePickerDialog(EventFormActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void setTime(View v) {
        new TimePickerDialog(EventFormActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

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

    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };
}
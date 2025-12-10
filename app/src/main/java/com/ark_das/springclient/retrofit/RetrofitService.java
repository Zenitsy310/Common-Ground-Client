package com.ark_das.springclient.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    // ВАЖНО: URL должен содержать две косые черты после протокола,
    // например, "http://192.168.0.144:9000/" или "http://192.168.0.144:9000"
    // Я исправляю ошибку в вашем URL, добавляя '://'
    private Retrofit retrofit;
    private final String base_url = "http://192.168.0.144:9000";

    public RetrofitService(){
        initializeRetrofit();
    }

    public void initializeRetrofit(){

        // 1. Создаем десериализатор для LocalDateTime
        // Он будет обрабатывать строку типа "YYYY-MM-DDTHH:mm:ss"
        JsonDeserializer<LocalDateTime> localDateTimeDeserializer = (json, type, context) -> {
            // Используем стандартный форматтер ISO_LOCAL_DATE_TIME, который обрабатывает "T"
            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        };

        // 2. Создаем настроенный объект Gson
        Gson gson = new GsonBuilder()
                // Регистрируем десериализатор для типа LocalDateTime
                .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer)
                // Опционально: если вы хотите также сериализовать (отправлять) время в том же формате
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer()) // Требуется дополнительный класс
                .create();

        // 3. Инициализируем Retrofit с настроенным Gson
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                // Используем наш настроенный Gson
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

    // Вспомогательный класс для сериализации (отправки данных)
    // Это гарантирует, что LocalDateTime будет корректно преобразован в строку для EventRequest
    private static class LocalDateTimeSerializer implements com.google.gson.JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public com.google.gson.JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(FORMATTER.format(src));
        }
    }
}
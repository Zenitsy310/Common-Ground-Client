package com.ark_das.springclient.retrofit;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;
    private final String base_url = "http:192.168.0.145:9000" ;// "http://192.168.43.238:9000"

    public RetrofitService(){
        initializeRetrofit();
    }

    public void initializeRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }
}

package com.ark_das.springclient.retrofit;

import com.ark_das.springclient.adapter.LoginRequest;
import com.ark_das.springclient.adapter.LoginResponse;
import com.ark_das.springclient.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApi {

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @POST("/user/save")
    Call<User> save(@Body User user);

    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/user/register")
    Call<LoginResponse> register(@Body User user);


}

package com.ark_das.springclient.retrofit;

import com.ark_das.springclient.dto.LoginRequest;
import com.ark_das.springclient.dto.LoginResponse;
import com.ark_das.springclient.dto.UserRequest;
import com.ark_das.springclient.dto.UserResponse;
import com.ark_das.springclient.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @POST("/user/save")
    Call<UserResponse> save(@Body User user);

    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/user/register")
    Call<LoginResponse> register(@Body User user);

    @POST("/user/get-by-id")
    Call<UserResponse> getById(@Body UserRequest userRequest);

    @DELETE("/user/{id}")
    Call<UserResponse> deleteById(@Path("id")int userId);

}

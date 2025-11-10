package com.ark_das.springclient.retrofit;

import com.ark_das.springclient.model.Role;
import com.ark_das.springclient.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RoleApi {


    @GET("role/get-all")
    Call<List<Role>> getAllRoles();


}

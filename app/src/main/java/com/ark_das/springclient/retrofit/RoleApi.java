package com.ark_das.springclient.retrofit;

import com.ark_das.springclient.dto.RoleResponse;
import com.ark_das.springclient.model.Role;
import com.ark_das.springclient.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RoleApi {


    @GET("/role/get-all")
    Call<List<Role>> getAllRoles();

    @GET("/role/{id}")
    Call <RoleResponse> getRoleById(@Path("id") int id);


}

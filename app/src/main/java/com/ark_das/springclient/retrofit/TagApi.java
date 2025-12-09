package com.ark_das.springclient.retrofit;

import com.ark_das.springclient.model.Tag;

import java.util.List;

import javax.security.auth.callback.Callback;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TagApi {

    @GET("/tag/get-all")
    Call<List<Tag>> getAllTags();

}

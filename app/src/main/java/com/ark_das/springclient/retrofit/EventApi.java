package com.ark_das.springclient.retrofit;

import com.ark_das.springclient.dto.EventRequest;
import com.ark_das.springclient.dto.EventResponse;
import com.ark_das.springclient.model.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventApi {

    @GET("/event/get-all")
    Call<List<Event>> getAllEvents();

    @POST("/event/save")
    Call<Event> saveEvent(Event event);

    @DELETE("/event/{id}")
    Call<Event> deleteEventById(@Path("id") int id);

    @GET("/event/{id}")
    Call<EventResponse> getEventById(@Path("id") int eventId);

    @POST("/event/save-with-tags")
    Call<EventResponse> saveEventWithTags(@Body EventRequest eventRequest);

}

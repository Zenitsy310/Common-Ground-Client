package com.ark_das.springclient.dto;

import com.ark_das.springclient.model.Event;

public class EventResponse {
    private boolean success;

    private String message;

    private Event event;

    public EventResponse() {

    }

    public EventResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public EventResponse(boolean success, String message, Event event) {
        this.success = success;
        this.message = message;
        this.event = event;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}

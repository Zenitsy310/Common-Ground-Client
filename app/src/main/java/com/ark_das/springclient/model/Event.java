package com.ark_das.springclient.model;

import java.time.LocalDateTime;

public class Event {
    private int id;

    private String title;

    private String description;

    private String place;

    private LocalDateTime time;

    private int create_by;

    private LocalDateTime created_at;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getCreate_by() {
        return create_by;
    }
    public void setCreate_by(int create_by) {
        this.create_by = create_by;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

}

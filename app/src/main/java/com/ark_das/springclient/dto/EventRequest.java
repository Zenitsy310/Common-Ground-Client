package com.ark_das.springclient.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class EventRequest {

    private int id;

    private String title;
    private String description;
    private String place;
    private LocalDateTime time;
    private int create_by;
    private Set<String> tagNames; // используем Set<String> для названий тегов

    // Конструкторы
    public EventRequest() {}

    public EventRequest(String title, String description, String place,
                        LocalDateTime time, int create_by, Set<String> tagNames) {
        this.title = title;
        this.description = description;
        this.place = place;
        this.time = time;
        this.create_by = create_by;
        this.tagNames = tagNames;
    }

    public EventRequest(String title, String description, String address, LocalDateTime time, Set<String> tagNames) {
        this.title = title;
        this.description = description;
        this.place = address;
        this.time = time;
        this.create_by = create_by;
        this.tagNames = tagNames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Геттеры и сеттеры
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

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }

}

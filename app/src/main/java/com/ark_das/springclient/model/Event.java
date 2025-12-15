package com.ark_das.springclient.model;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Event {
    private int id;

    private String title;

    private String description;

    private String place;

    private LocalDateTime time;

    private int create_by;

    private LocalDateTime created_at;

    private Set<Tag> tags = new HashSet<>();
    private Set<User> members = new HashSet<>();

    public void setMembers(Set<User> user) {
        members.clear();
        members.addAll(user);
    }

    public void deleteMember(User user) {
        members.removeIf(t -> t.equals(user));
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setTags(Set<Tag> tag) {
        tags.clear();
        tags.addAll(tag);
    }

    public void deleteTag(Tag tag) {
        tags.removeIf(t -> t.equals(tag));
    }

    public Set<Tag> getTags() {
        return tags;
    }


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

    public void setCreated_at(LocalDateTime create_at) {
        this.created_at = create_at;
    }
}

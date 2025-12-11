package com.ark_das.springclient.model;

import java.time.LocalDateTime;
import java.util.Set;

public class User {
	private int id;
	private String first_name;
	 private String last_name;
	 private String email;
	 private String password;
	 private String bio;
	 private int role_id;
	 private String img_data_avatar;
	 private String created_at;
	 private String login;

    private Set<Event> events;

    public void setEvents(Set<Event> event) {
        events.clear();
        events.addAll(event);
    }

    public void deleteEvent(Event event) {
        events.removeIf(t -> t.equals(event));
    }

    public Set<Event> getEvents() {
        return events;
    }


    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	
	public String getImg_data_avatar() {
		return img_data_avatar;
	}
	public void setImg_data_avatar(String img_data_avatar) {
		this.img_data_avatar = img_data_avatar;
	}
	
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", first_name=" + first_name + ", last_name=" + last_name + ", email=" + email
				+ ", password=" + password + ", bio=" + bio + ", role_id=" + role_id + ", img_data_avatar="
				+ img_data_avatar + ", created_at=" + created_at + "]";
	}



}

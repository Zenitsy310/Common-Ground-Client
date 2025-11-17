package com.ark_das.springclient.dto;

public class UserRequest {

    private int id;

    public UserRequest(){

    }
    public UserRequest(int id){
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



}

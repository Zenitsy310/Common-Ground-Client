package com.ark_das.springclient.dto;

import com.ark_das.springclient.model.Role;

public class RoleResponse {

    private boolean success;

    private String message;

    private Role role;

    public RoleResponse() {

    }

    public RoleResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RoleResponse(boolean success, String message,Role role) {
        this.success = success;
        this.message = message;
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}

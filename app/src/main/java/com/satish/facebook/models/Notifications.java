package com.satish.facebook.models;

import java.sql.Timestamp;

/**
 * Created by satish on 24/9/15.
 */
public class Notifications {
    private int user_id,status,post_id;
    private String user_name, message, profile_image;
    private Timestamp created_at;

    public Notifications() {
    }

    public Notifications(int user_id, int status, String user_name, String message, String profile_image, Timestamp created_at,int post_id) {
        this.user_id = user_id;
        this.status = status;
        this.user_name = user_name;
        this.message = message;
        this.profile_image = profile_image;
        this.created_at = created_at;
        this.post_id=post_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }
}

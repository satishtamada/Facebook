package com.satish.facebook.models;

import java.sql.Timestamp;

/**
 * Created by satish on 2/9/15.
 */
public class Comments {
    private int commented_user_id;
    private String commented_username, comment,profile_image;
    private Timestamp created_at;

    public Comments() {

    }

    public Comments( int commented_user_id, int like_status,
                    String commented_username, String comment, Timestamp created_at,String profile_image) {
        this.commented_user_id = commented_user_id;
        this.commented_username = commented_username;
        this.comment = comment;
        this.created_at = created_at;
        this.profile_image=profile_image;
    }

    public int getCommented_user_id() {
        return commented_user_id;
    }

    public void setCommented_user_id(int commented_user_id) {
        this.commented_user_id = commented_user_id;
    }

    public String getCommented_username() {
        return commented_username;
    }

    public void setCommented_username(String commented_username) {
        this.commented_username = commented_username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}

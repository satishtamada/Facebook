package com.satish.facebook.models;

/**
 * Created by satish on 24/8/15.
 */
public class Feed {
    private String name, profileImageUrl, status, image, created_at, url;
    private int post_id, comments_count;

    public Feed() {
    }

    public Feed(String name, String profileImageUrl, String status, String image, String created_at, String url, int post_id, int comments_count) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.image = image;
        this.created_at = created_at;
        this.url = url;
        this.post_id = post_id;
        this.comments_count = comments_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String text) {
        this.status = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }
}

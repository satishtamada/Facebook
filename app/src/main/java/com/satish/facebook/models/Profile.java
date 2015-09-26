package com.satish.facebook.models;

/**
 * Created by satish on 26/9/15.
 */
public class Profile {

    String id, name, email, profile_image_url, created_at, apikey;


    public Profile() {
    }

    public Profile(String id, String name, String email, String profile_image_url, String created_at, String apikey) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profile_image_url = profile_image_url;
        this.created_at = created_at;
        this.apikey = apikey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
}

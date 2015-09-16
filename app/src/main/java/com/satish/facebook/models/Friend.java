package com.satish.facebook.models;

/**
 * Created by satish on 18/8/15.
 */
public class Friend {
    private String name;
    private String id;
    private String profileImageUrl;

    public Friend() {
    }

    public Friend(String name, String id, String profileImageUrl) {
        this.name = name;
        this.id = id;
        this.profileImageUrl = profileImageUrl;
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

    public String getId() {
        return id;

    }

    public void setId(String id) {
        this.id = id;
    }
}

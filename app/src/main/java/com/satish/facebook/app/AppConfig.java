package com.satish.facebook.app;

import java.util.Arrays;
import java.util.List;

/**
 * Created by satish on 6/8/15.
 */
public class AppConfig {
    public static String URL="http://192.168.0.102:8080/facebook_rest/rest/";
    public static String URL_REGISTER = URL+"user/register";
    public static String URL_LOGIN = URL+"user/login";
    public static String URL_FIND_FRIEND = URL+"friend/find_friend";
    public static String URL_FRIENDS_LIST = URL+"friend/friends_list";
    public static String URL_HOME = URL+"friend/friend_posts";
    //public static String URL_USER_POSTS = URL+"friend/user_posts?id=104";
    public static String URL_FEED_CREATE =URL+ "feed/create";
    public static String URL_USER_PROFILE = URL+"friend/profile";
    public static String URL_POST_COMMENTS=URL+"post/comments";

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // SD card image directory
    public static final String PHOTO_ALBUM = "androidhive";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
            "png");
}

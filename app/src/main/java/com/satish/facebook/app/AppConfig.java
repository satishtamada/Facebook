package com.satish.facebook.app;

import java.util.Arrays;
import java.util.List;

/**
 * Created by satish on 6/8/15.
 */
public class AppConfig {
    public static final String PARSE_CHANNEL = "AndroidHive";
    public static final String PARSE_APPLICATION_ID = "3M4IbwBiVGDiJLLRWbaUWDbBujhyI4mj0R2U7Fvg";
    public static final String PARSE_CLIENT_KEY = "ZxzSclm7HIMDAYYbxQ7wjDhmy0ed6kTopra8neV8";
    public static final int NOTIFICATION_ID = 100;

    public static String URL = "http://192.168.0.103:8080/facebook_rest/rest/";
    public static String URL_LOGIN = URL + "user/login";
    public static String URL_REGISTER = URL + "user/register";
    public static String URL_PROFILE_UPLOAD = URL + "user/profile_upload";
    public static String URL_HOME = URL + "friend/feed";
    public static String URL_FRIEND_SUGGESTIONS = URL + "friend/friend_suggestions";
    public static String URL_FRIENDS_LIST = URL + "friend/friends_list";
    public static String URL_FEED_CREATE = URL + "feed/create";
    public static String URL_USER_PROFILE = URL + "friend/profile";
    public static String URL_COMMENTS = URL + "post/comments";
    public static String URL_POST_COMMENTS = URL + "comment/create";

    public static String URL_FRIEND_ADD = URL + "friend/add_friend";
    public static String URL_FRIEND_REQUESTS = URL + "friend/friend_requests";
    public static String URL_FRIEND_REQUEST_ACCEPT = URL + "friend/add_friend_confirm";
    public static String URL_FRIEND_REQUEST_DELETE = URL + "friend/add_friend_delete";

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // SD card image directory
    public static final String PHOTO_ALBUM = "facebook";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
            "png");

    // notificaitons changes

    public static final int FRIEND_STATUS_WAITIN_CONFIRMATION = 0;
    public static final int FRIEND_STATUS_CONFIRMED = 1;
    public static final int FRIEND_STATUS_DELETED = 2;
    public static final int REQUEST_STATUS_CONFIRMED=3;

    // Push notification flag values
    public static final int FLAG_NEW_FRIEND_REQUEST = 1;
    public static final int FLAG_NEW_LIKE = 2;
    public static final int FLAG_NEW_COMMENT = 3;



}

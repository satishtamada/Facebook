package com.satish.facebook.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.satish.facebook.models.Feed;
import com.satish.facebook.models.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by satish on 6/8/15.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "facebook_api";

    // Login table name
    private static final String TABLE_USER = "user";

    //Feed table name

    private static final String TABLE_FEED = "feed";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_FEED_IMAGE = "feed_image";
    private static final String KEY_FEED_TEXT = "feed_status";
    private static final String KEY_POST_ID = "post_id";
    private static final String KEY_COMMENT_COUNT = "comment_count";
    private static final String KEY_LIKE_STATUS = "like_status";

    private static final String CREATE_FEED_TABLE = "CREATE TABLE " + TABLE_FEED + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PROFILE_IMAGE + " TEXT,"
            + KEY_FEED_TEXT + " TEXT," + KEY_FEED_IMAGE + " TEXT," + KEY_POST_ID + " INTEGER UNIQUE,"
            + KEY_COMMENT_COUNT + " INTEGER," + KEY_LIKE_STATUS + " INTEGER," + KEY_CREATED_AT + " TEXT" + ")";


    //user table
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_API_KEY + " TEXT," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
            + KEY_CREATED_AT + " TEXT," + KEY_PROFILE_IMAGE + " TEXT" + ")";
    //feed table

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_FEED_TABLE);
        Log.d(TAG, "Database tables created");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, String uid, String api_key, String created_at, String profile_image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // id
        values.put(KEY_API_KEY, api_key);//api_key
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_PROFILE_IMAGE, profile_image);//profile_image

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing user details in database
     */
    public void addFeed(String name, String profileImage, String feedStatus, String feedImage, int post_id, int commentCount, int like_status, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_PROFILE_IMAGE, profileImage); // profile
        values.put(KEY_FEED_TEXT, feedStatus); // feed status
        values.put(KEY_FEED_IMAGE, feedImage);//feed image
        values.put(KEY_POST_ID, post_id);//post id
        values.put(KEY_COMMENT_COUNT, commentCount); //comments count
        values.put(KEY_LIKE_STATUS, like_status);// like status
        values.put(KEY_CREATED_AT, created_at); // Created At
        try {
            // Inserting Row
            long id = db.insert(TABLE_FEED, null, values);
            Log.d(TAG, "New feed inserted into sqlite: " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close(); // Closing database connection
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("api_key", cursor.getString(1));
            user.put("uid", cursor.getString(2));
            user.put("name", cursor.getString(3));
            user.put("email", cursor.getString(4));
            user.put("created_at", cursor.getString(5));
            user.put("profile_image", cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public Profile getUserProfile() {

        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();

        Profile profile = new Profile();

        if (cursor.getCount() > 0) {

            profile.setId(cursor.getString(2));
            profile.setApikey(cursor.getString(1));
            profile.setName(cursor.getString(3));
            profile.setEmail(cursor.getString(4));
            profile.setCreated_at(cursor.getString(5));
            profile.setProfile_image_url(cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return profile;
    }

    public List<Feed> getFeed() {
        ArrayList<Feed> feedArrayList;
        String selectQuery = "SELECT  * FROM " + TABLE_FEED + " order by " + KEY_POST_ID + " desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        feedArrayList = new ArrayList<Feed>();
        Log.d("cursor size is", "" + cursor.getCount());

        if (cursor.getCount() > 0) {
            do {
                Log.d(TAG, "in while loop");
                Log.d("result", Integer.toString(cursor.getInt(5)));
                Feed userFeed = new Feed();
                userFeed.setName(cursor.getString(1));
                userFeed.setProfileImageUrl(cursor.getString(2));
                userFeed.setStatus(cursor.getString(3));
                if (cursor.getString(4).length() != 0) {
                    userFeed.setImage(cursor.getString(4));
                }
                userFeed.setPost_id(cursor.getInt(5));
                userFeed.setComments_count(cursor.getInt(6));
                userFeed.setLike_status(cursor.getInt(7));
                userFeed.setCreated_at(cursor.getString(8));
                feedArrayList.add(userFeed);
            } while (cursor.moveToNext());
        }


        cursor.close();
        return feedArrayList;


    }

    /**
     * Getting user login status return true if rows are there in table
     */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_FEED,null,null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }


    public void getFeedDetails() {
        HashMap<String, String> Feed = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_FEED;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Log.d("result", cursor.getString(1));
            Log.d("result", cursor.getString(2));
            Log.d("result", cursor.getString(3));
            Log.d("result", cursor.getString(4));
            Log.d("result", Integer.toString(cursor.getInt(5)));
            Log.d("result", Integer.toString(cursor.getInt(6)));
            Log.d("result", Integer.toString(cursor.getInt(7)));
            Log.d("result", cursor.getString(8));
        }
        cursor.close();

    }
}

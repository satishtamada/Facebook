package com.satish.facebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by satish on 12/9/15.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static String tag = "json_tag";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private ProgressBar progressBar;
    private TextView lblUserName, lblUserMailId, lblUserJoined, lblFriendCount, lblPostCount, lblPictureCount;
    private RelativeLayout parentLayout, userProfileLayout, friendsLayout, findFriendsLayout, pictureLayout, logoutLayout;
    private NetworkImageView profile_image;
    private ArrayList<String> userImages;
    private ArrayList<String> postIds;
    private String userName;
    private int postCount;
    private String id;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        lblUserName = (TextView) view.findViewById(R.id.username);
        lblUserMailId = (TextView) view.findViewById(R.id.user_mailId);
        lblUserJoined = (TextView) view.findViewById(R.id.userJoined);
        lblFriendCount = (TextView) view.findViewById(R.id.friendsCount);
        lblPostCount = (TextView) view.findViewById(R.id.postCount);
        lblPictureCount = (TextView) view.findViewById(R.id.imageCount);
        profile_image = (NetworkImageView) view.findViewById(R.id.profile_image);
        userProfileLayout = (RelativeLayout) view.findViewById(R.id.user_profile);
        parentLayout = (RelativeLayout) view.findViewById(R.id.profile_parent_layout);
        friendsLayout = (RelativeLayout) view.findViewById(R.id.friendsLayout);
        findFriendsLayout = (RelativeLayout) view.findViewById(R.id.findFriendsLayout);
        pictureLayout = (RelativeLayout) view.findViewById(R.id.picturesLayout);
        logoutLayout = (RelativeLayout) view.findViewById(R.id.logoutLayout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        userImages = new ArrayList<>();
        postIds = new ArrayList<>();
        db = new SQLiteHandler(getActivity());
        // session manager
        session = new SessionManager(getActivity());


        friendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendsIntent = new Intent(getActivity(), FriendsHandlerActivity.class);
                friendsIntent.putExtra("tab_name", 0);
                startActivity(friendsIntent);
            }
        });
        findFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findFriendsIntent = new Intent(getActivity(), FriendsHandlerActivity.class);
                findFriendsIntent.putExtra("tab_name", 1);
                startActivity(findFriendsIntent);
            }
        });
        pictureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postCount != 0) {
                    Intent pictureIntent = new Intent(getActivity(), ImagesActivity.class);
                    pictureIntent.putExtra("postedImages", userImages);
                    pictureIntent.putExtra("postImagesIds", postIds);
                    pictureIntent.putExtra("userName", userName);
                    startActivity(pictureIntent);
                }
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();

            }
        });
        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");

        String url = AppConfig.URL_USER_PROFILE;
        url += "?id=" + id;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            boolean error = response.getBoolean("success");
                            if (error) {
                                JSONObject jsonObject = response.getJSONObject("profile");
                                profile_image.setImageUrl(jsonObject.getString("profile_image"), imageLoader);
                                userName = toTitleCase(jsonObject.getString("name"));
                                lblUserName.setText(userName);
                                Log.d("name is", userName);
                                lblUserMailId.setText(jsonObject.getString("email"));
                                String date="";
                                date=jsonObject.getString("created_at").substring(0,10);
                                lblUserJoined.setText(date);
                                lblFriendCount.setText(jsonObject.getString("friends_count"));
                                lblPostCount.setText(jsonObject.getString("posts_count"));
                                //set user pics to in images array
                                postCount = Integer.parseInt(jsonObject.getString("posts_count"));
                                if (Integer.parseInt(jsonObject.getString("posts_count")) != 0) {
                                    JSONArray jsonArray = response.getJSONArray("posts");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                        if (friendObj.getString("image").length() != 0) {
                                            userImages.add(friendObj.getString("image"));
                                            postIds.add(friendObj.getString("post_id"));
                                        }

                                    }
                                    lblPictureCount.setText(Integer.toString(userImages.size()));
                                    Log.d("image array is", userImages.toString());
                                }
                                progressBar.setVisibility(View.GONE);
                                parentLayout.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), "server busy", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                progressBar.setVisibility(View.VISIBLE);
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

        return view;
    }

    private String toTitleCase(String name) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : name.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    private void logoutUser() {
        AppController.getInstance().logout();
    }
}

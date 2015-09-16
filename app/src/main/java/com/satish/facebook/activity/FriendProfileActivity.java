package com.satish.facebook.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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
import com.satish.facebook.adapters.FeedAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Feed;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by satish on 22/8/15.
 */
public class FriendProfileActivity extends AppCompatActivity {
    private ArrayList<Feed> feedArrayList;
    private ProgressDialog pDialog;
    private FeedAdapter feedAdapter;
    private static String tag = "json_tag";
    private static final String TAG = HomePageActivity.class.getSimpleName();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private NetworkImageView profileImage;
    private String userName;
    public static int postCount;
    private ListView listView;
    private ActionBar actionBar;
    private TextView lblUserName;
    private View header;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //creating listview instance
        listView = (ListView) findViewById(R.id.friend_posts);
        //user profile header
        header = getLayoutInflater().inflate(R.layout.header_friend_profile, null);
        lblUserName = (TextView) header.findViewById(R.id.friendName);
        profileImage = (NetworkImageView) header.findViewById(R.id.profile_image);
        feedArrayList = new ArrayList<>();
        feedAdapter = new FeedAdapter(this, feedArrayList);
        //set adapter to listview
        listView.setAdapter(feedAdapter);
        //display prograss dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_USER_PROFILE, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            boolean error = response.getBoolean("success");
                            if (error) {
                                listView.addHeaderView(header);
                                JSONObject jsonObject = response.getJSONObject("profile");
                                profileImage.setImageUrl(jsonObject.getString("profile_image"), imageLoader);
                                lblUserName.setText(jsonObject.getString("name"));
                                //set name to actionbar title
                                actionBar.setTitle(jsonObject.getString("name"));
                                postCount = Integer.parseInt(jsonObject.getString("posts_count"));
                                //checking user have any posts then it will display on listview
                                if (postCount != 0) {
                                    JSONArray jsonArray = response.getJSONArray("posts");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                        Feed p = new Feed();
                                        p.setName(jsonObject.getString("name"));
                                        p.setProfileImageUrl(jsonObject.getString("profile_image"));
                                        p.setImage(friendObj.getString("image"));
                                        p.setStatus(friendObj.getString("text"));
                                        p.setCreated_at(friendObj.getString("created_at"));
                                        feedArrayList.add(p);
                                        Log.d("frined object", p.getProfileImageUrl() + p.getStatus() + p.getImage());
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "No posts", Toast.LENGTH_LONG).show();
                                }
                                pDialog.hide();
                            } else {
                                Toast.makeText(getApplicationContext(), "server busy", Toast.LENGTH_LONG).show();
                                pDialog.hide();
                            }

                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                        feedAdapter.notifyDataSetChanged();
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }
}

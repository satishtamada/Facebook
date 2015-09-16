package com.satish.facebook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
 * Created by satish on 24/8/15.
 */
public class HomePageActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Feed> feedArrayList;
    private ProgressDialog pDialog;
    private static final String TAG = HomePageActivity.class.getSimpleName();
    private FeedAdapter feedAdapter;
    private static String tag = "json_tag";
    private Button btnStatusText, btnStatusImage,btnWhatOnYourMind;
    private NetworkImageView profileImage;
    private static int RESULT_LOAD_IMAGE = 1;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String userProfileImage,userName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //creating listview instance
        listView = (ListView) findViewById(R.id.friend_posts);
        feedArrayList = new ArrayList<>();
        View header = (View) getLayoutInflater().inflate(R.layout.home_header_layout, null);
        profileImage = (NetworkImageView) header.findViewById(R.id.user_profile_image);
        //set event on button status
        btnStatusText = (Button) header.findViewById(R.id.btn_status);
        btnStatusImage = (Button) header.findViewById(R.id.btn_status_image);
        btnWhatOnYourMind= (Button) header.findViewById(R.id.btn_whats_on_your_mind);
        btnStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent = new Intent(getApplicationContext(), FeedPostActivity.class);
                statusIntent.putExtra("userProfileImage",userProfileImage);
                statusIntent.putExtra("userName",userName);
                startActivity(statusIntent);
            }
        });
        btnWhatOnYourMind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIntent = new Intent(getApplicationContext(), FeedPostActivity.class);
                statusIntent.putExtra("userProfileImage",userProfileImage);
                statusIntent.putExtra("userName",userName);
                startActivity(statusIntent);
            }
        });
        btnStatusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "select image", Toast.LENGTH_LONG).show();
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        listView.addHeaderView(header);
        feedAdapter = new FeedAdapter(this, feedArrayList);
        //set adapter to listview
        listView.setAdapter(feedAdapter);
        //display prograss dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.URL_HOME, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {

                            boolean error = response.getBoolean("success");
                            if (error) {
                                profileImage.setImageUrl(response.getString("profile_image"), imageLoader);
                                userProfileImage=response.getString("profile_image");
                                userName=response.getString("username");
                                Log.d("user profile", response.getString("profile_image"));
                                JSONArray jsonArray = response.getJSONArray("posts");
                                Log.d(TAG, "size is" + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                    String name = friendObj.getString("username");
                                    String profile_imamge = friendObj.getString("profile_image");
                                    String post_image = friendObj.getString("image");
                                    String created_at = friendObj.getString("created_at");
                                    String postText = friendObj.getString("text");
                                    Log.d("image", profile_imamge);
                                    Log.d(TAG, friendObj.toString());
                                    Feed feed = new Feed();
                                    feed.setName(name);
                                    feed.setProfileImageUrl(profile_imamge);
                                    feed.setCreated_at(created_at);
                                    feed.setStatus(postText);
                                    feed.setImage(post_image);
                                    feedArrayList.add(feed);
                                }
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

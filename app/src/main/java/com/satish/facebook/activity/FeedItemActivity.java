package com.satish.facebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.adapters.CommentAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.FeedImageView;
import com.satish.facebook.models.Comments;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by satish on 25/9/15.
 */
public class FeedItemActivity extends AppCompatActivity {
    private static final String TAG = FeedItemActivity.class.getSimpleName();
    private static String tag = "json_tag";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    TextView lblName, lblTimestamp, lblStatusMsg, lblCommentsCount;
    FeedImageView feedImageView;
    String userName;
    Timestamp timestamp;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private NetworkImageView profile_image;
    private CommentAdapter commentAdapter;
    private LinearLayout feedItem;
    private ArrayList<Comments> commetArrayList;
    private ListView listView;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_item_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lblName = (TextView) findViewById(R.id.name);
        feedItem = (LinearLayout) findViewById(R.id.feed_item);
        lblTimestamp = (TextView) findViewById(R.id.timestamp);
        lblStatusMsg = (TextView) findViewById(R.id.txtStatusMsg);
        lblCommentsCount = (TextView) findViewById(R.id.comments_count);
        profile_image = (NetworkImageView) findViewById(R.id.profilePic);
        feedImageView = (FeedImageView) findViewById(R.id.feedImage1);
        listView = (ListView) findViewById(R.id.comment_list_view);
        commetArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commetArrayList, this);
        listView.setAdapter(commentAdapter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Intent i = getIntent();
        int post_id = i.getIntExtra("post_id", 0);
        String url = AppConfig.URL_FEED_ITEM;
        url += "?post_id=" + Integer.toString(post_id);
        Log.d(TAG, url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            boolean error = response.getBoolean("success");
                            if (error) {
                                profile_image.setImageUrl(response.getString("profile_image"), imageLoader);
                                userName = toTitleCase(response.getString("user_name"));
                                lblName.setText(userName);
                                lblTimestamp.setText(response.getString("created_at"));
                                lblCommentsCount.setText(response.getString("comments_count"));
                                // Check for empty status message
                                if (!TextUtils.isEmpty(response.getString("text"))) {
                                    lblStatusMsg.setText(response.getString("text"));
                                    lblStatusMsg.setVisibility(View.VISIBLE);
                                } else {
                                    // status is empty, remove from view
                                    lblStatusMsg.setVisibility(View.GONE);
                                }
                                //set feed image to feed image view
                                if (response.getString("image") != null) {
                                    feedImageView.setImageUrl(response.getString("image"), imageLoader);
                                    feedImageView.setVisibility(View.VISIBLE);
                                    feedImageView
                                            .setResponseObserver(new FeedImageView.ResponseObserver() {
                                                @Override
                                                public void onError() {
                                                }

                                                @Override
                                                public void onSuccess() {
                                                }
                                            });
                                } else {
                                    feedImageView.setVisibility(View.GONE);
                                }
                                feedItem.setVisibility(View.VISIBLE);
                                //setting comments to comment listview
                                JSONArray jsonArray = response.getJSONArray("comments");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                        String id = friendObj.getString("user_id");
                                        String name = friendObj.getString("username");
                                        String image = friendObj.getString("profile_image");
                                        String comment_text = friendObj.getString("comment");
                                        String created_at = friendObj.getString("created_at");
                                        String userName = toTitleCase(name);
                                        try {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                                            Date parsedDate = dateFormat.parse(created_at);
                                            timestamp = new java.sql.Timestamp(parsedDate.getTime());
                                        } catch (Exception e) {
                                        }
                                        Log.d(TAG, id + name);
                                        Log.d("image", image);
                                        Comments comment = new Comments();
                                        comment.setCommented_user_id(Integer.parseInt(id));
                                        comment.setCommented_username(userName);
                                        comment.setProfile_image(image);
                                        comment.setComment(comment_text);
                                        comment.setCreated_at(timestamp);
                                        commetArrayList.add(comment);
                                    }
                                }
                            }//end of if error checking
                        } //end of try
                        catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                        if (commetArrayList.size() > 0) {
                            commentAdapter.notifyDataSetChanged();
                            listView.setVisibility(View.VISIBLE);
                            setListViewHeightBasedOnChildren(listView);
                        } else {
                            progressBar.setVisibility(View.GONE);

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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);


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
}

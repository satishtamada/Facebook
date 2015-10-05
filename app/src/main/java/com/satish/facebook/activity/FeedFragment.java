package com.satish.facebook.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.satish.facebook.R;
import com.satish.facebook.adapters.FeedAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.PrefManager;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.helper.Utils;
import com.satish.facebook.helper.VolleyNetwork;
import com.satish.facebook.models.Feed;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FeedFragment.class.getSimpleName();
    private static String tag = "json_tag";
    private ListView listView;
    private List<Feed> feedArrayList;
    private ProgressBar progressBar;
    private FeedAdapter feedAdapter;
    private SQLiteHandler db;
    private String id;
    private LinearLayout noFeedLayout;
    private Button btnFindFriends;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String feedUrl;
    String date1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        noFeedLayout = (LinearLayout) view.findViewById(R.id.noFeedLayout);
        btnFindFriends = (Button) view.findViewById(R.id.find_friends);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        //creating listview instance
        listView = (ListView) view.findViewById(R.id.feed_list);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        feedArrayList = new ArrayList<>();
        feedAdapter = new FeedAdapter(getActivity(), feedArrayList);

        toggleListVisibility();

        //set adapter to listview
        listView.setAdapter(feedAdapter);
        db = new SQLiteHandler(getActivity());
        feedArrayList.clear();
        feedArrayList.addAll(db.getFeed());
        feedAdapter.notifyDataSetChanged();
        toggleListVisibility();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Feed feed = feedArrayList.get(position);

                Intent i = new Intent(getActivity(), FeedItemActivity.class);
                i.putExtra("post_id", feed.getPost_id());
                startActivity(i);

            }
        });

        btnFindFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FriendsHandlerActivity.class);
                i.putExtra("tab_name", 2);
                startActivity(i);
            }
        });
        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");
        String url = AppConfig.URL_HOME;
        url += "?id=" + id;
        swipeRefreshLayout.setOnRefreshListener(this);
        feedUrl = url;
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //swipeRefreshLayout.setRefreshing(true);
                                        fetchFeed(feedUrl, false);
                                    }
                                }
        );

        return view;
    }

    private void fetchFeed(String finalUrl, boolean isForceLoad) {

        Log.d(TAG, "currentTime is" + date1);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                finalUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            boolean error = response.getBoolean("success");
                            if (error) {
                                JSONArray jsonArray = response.getJSONArray("posts");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                    String name = friendObj.getString("username");
                                    String profile_image = friendObj.getString("profile_image");
                                    String post_image = friendObj.getString("image");
                                    String created_at = friendObj.getString("created_at");
                                    String postText = friendObj.getString("text");
                                    int postId = friendObj.getInt("post_id");
                                    int commentsCount = friendObj.getInt("comments_count");
                                    int like_status = friendObj.getInt("like_status");
                                    String userName = toTitleCase(name);
                                    db.addFeed(userName, profile_image, postText, post_image, postId, commentsCount, like_status, created_at);
                                    //Feed feed = new Feed();
                                    //feed.setName(userName);
                                    //  feed.setProfileImageUrl(profile_image);
                                    //feed.setCreated_at(created_at);
                                    //feed.setStatus(postText);
                                    // feed.setPost_id(postId);
                                    //feed.setComments_count(commentsCount);
                                    //feed.setLike_status(like_status);
                                    //if (friendObj.getString("image").length() != 0) {
                                    //   feed.setImage(post_image);
                                    // }

                                }
                                //feedArrayList = db.getFeed();
                                feedArrayList.clear();
                                feedArrayList.addAll(db.getFeed());
                                //  db.getFeedDetails();

                                PrefManager pref = new PrefManager(getActivity());
                                pref.storeLastFeedRequestTime();

                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                listView.setVisibility(View.GONE);
                                noFeedLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }

                        toggleListVisibility();
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.e(TAG, "Headers: " + VolleyNetwork.getHeaders());
                return VolleyNetwork.getHeaders();
            }

        };

        if (isForceLoad || Utils.isFeedRequestTimeout(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag);
        }
    }

    private void toggleListVisibility() {
        if (feedArrayList.size() > 0) {
            Log.d(TAG, Integer.toString(feedArrayList.size()));
            feedAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            noFeedLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);


        } else {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            noFeedLayout.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onRefresh() {
        fetchFeed(feedUrl, true);
    }
}

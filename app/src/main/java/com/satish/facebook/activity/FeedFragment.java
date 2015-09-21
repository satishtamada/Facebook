package com.satish.facebook.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.satish.facebook.R;
import com.satish.facebook.adapters.FeedAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.models.Feed;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedFragment extends Fragment {

    private ListView listView;
    private ArrayList<Feed> feedArrayList;
    private ProgressBar progressBar;
    private static final String TAG = FeedFragment.class.getSimpleName();
    private FeedAdapter feedAdapter;
    private static String tag = "json_tag";
    private SQLiteHandler db;
    private String id;
    private LinearLayout noFeedLayout;
    private Button btnFindFriends;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        noFeedLayout= (LinearLayout) view.findViewById(R.id.noFeedLayout);
        btnFindFriends= (Button) view.findViewById(R.id.find_friends);
        //creating listview instance
        listView = (ListView) view.findViewById(R.id.feed_list);
        progressBar = (ProgressBar)view. findViewById(R.id.progressBar);
        feedArrayList = new ArrayList<>();
        feedAdapter = new FeedAdapter(getActivity(), feedArrayList);
        //set adapter to listview
        listView.setAdapter(feedAdapter);
        db=new SQLiteHandler(getActivity());
        btnFindFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FriendsHandlerActivity.class);
                i.putExtra("tab_name",2);
                startActivity(i);
            }
        });

        //display prograss dialog

        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> user = db.getUserDetails();
        id=user.get("uid");
        String url = AppConfig.URL_HOME;
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
                                JSONArray jsonArray = response.getJSONArray("posts");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                    String name = friendObj.getString("username");
                                    String profile_image = friendObj.getString("profile_image");
                                    String post_image = friendObj.getString("image");
                                    String created_at = friendObj.getString("created_at");
                                    String postText = friendObj.getString("text");
                                    int postId=friendObj.getInt("post_id");
                                    int commentsCount=friendObj.getInt("comments_count");
                                    String userName = toTitleCase(name);
                                    Feed feed = new Feed();
                                    feed.setName(userName);
                                    feed.setProfileImageUrl(profile_image);
                                    feed.setCreated_at(created_at);
                                    feed.setStatus(postText);
                                    feed.setPost_id(postId);
                                    feed.setComments_count(commentsCount);
                                    if (friendObj.getString("image").length() != 0) {
                                        feed.setImage(post_image);
                                    }
                                    feedArrayList.add(feed);
                                }
                            }
                            else{
                                listView.setVisibility(View.GONE);
                                noFeedLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }

                        if(feedArrayList.size() > 0) {
                            feedAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            noFeedLayout.setVisibility(View.VISIBLE);
                        }
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                progressBar.setVisibility(View.GONE);
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

}

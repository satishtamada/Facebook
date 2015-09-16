package com.satish.facebook.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private ProgressDialog pDialog;
    private static final String TAG = HomePageActivity.class.getSimpleName();
    private FeedAdapter feedAdapter;
    private static String tag = "json_tag";
    private SQLiteHandler db;
    private String id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        //creating listview instance
        listView = (ListView) view.findViewById(R.id.feed_list);
        feedArrayList = new ArrayList<>();
        feedAdapter = new FeedAdapter(getActivity(), feedArrayList);
        //set adapter to listview
        listView.setAdapter(feedAdapter);
        db=new SQLiteHandler(getActivity());

        //display prograss dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading....");
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
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
                                    String postId=friendObj.getString("post_id");
                                    String userName = toTitleCase(name);
                                    Feed feed = new Feed();
                                    feed.setName(userName);
                                    feed.setProfileImageUrl(profile_image);
                                    feed.setCreated_at(created_at);
                                    feed.setStatus(postText);
                                    feed.setPost_id(Integer.parseInt(postId));
                                    if (friendObj.getString("image").length() != 0) {
                                        feed.setImage(post_image);
                                    }
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

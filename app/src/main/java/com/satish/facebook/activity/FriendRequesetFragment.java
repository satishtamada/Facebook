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
import com.satish.facebook.adapters.FriendRequestAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.models.Friend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by satish on 13/9/15.
 */
public class FriendRequesetFragment extends Fragment implements FriendRequestAdapter.FriendRequestAdapterListener {
    private static final String TAG = FriendRequesetFragment.class.getSimpleName();
    private static String tag = "json_tag";
    private ListView listView;
    private FriendRequestAdapter friendRequestAdapter;
    private ArrayList<Friend> friendArrayList;
    private ProgressBar progressBar;
    private String id;
    private SQLiteHandler db;
    private LinearLayout noRequestsLayout;
    private Button btnFindFriend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requestes, container, false);
        listView = (ListView) view.findViewById(R.id.friend_request_listview);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btnFindFriend = (Button) view.findViewById(R.id.btn_find_friend);
        noRequestsLayout = (LinearLayout) view.findViewById(R.id.noRequestsLayout);
        friendArrayList = new ArrayList<>();
        db = new SQLiteHandler(getActivity());
        progressBar.setVisibility(View.VISIBLE);
        btnFindFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findFriendsIntent = new Intent(getActivity(), FriendsHandlerActivity.class);
                findFriendsIntent.putExtra("tab_name", 1);
                startActivity(findFriendsIntent);
            }
        });
        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");
        String url = AppConfig.URL_FRIEND_REQUESTS;
        url += "?id=" + id;
        friendRequestAdapter = new FriendRequestAdapter(friendArrayList, getActivity(), id);
        friendRequestAdapter.setFriendRequestAdapterListener(this);
        listView.setAdapter(friendRequestAdapter);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            Log.d(TAG, "hello");
                            JSONArray jsonArray = response.getJSONArray("friends");
                            Log.d(TAG, "size is" + jsonArray);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                String id = friendObj.getString("id");
                                String username = friendObj.getString("username");
                                String image = friendObj.getString("profile_image");
                                String name = toTitleCase(username);
                                Friend friend = new Friend();
                                friend.setId(id);
                                friend.setName(name);
                                friend.setProfileImageUrl(image);
                                friend.setStatus(AppConfig.FRIEND_STATUS_WAITIN_CONFIRMATION);
                                friendArrayList.add(friend);
                            }

                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                        if (friendArrayList.size() > 0) {
                            friendRequestAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            noRequestsLayout.setVisibility(View.VISIBLE);

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

    @Override
    public void onFriendRequestConfirmed(int position) {
        Friend friend = friendArrayList.get(position);
        friend.setStatus(AppConfig.FRIEND_STATUS_CONFIRMED);
        friendArrayList.remove(position);
        friendArrayList.add(position, friend);
        friendRequestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFriendRequestConfirmError(String error) {

    }

    @Override
    public void onFriendRequestDeleted(int position) {
        friendArrayList.remove(position);
        friendRequestAdapter.notifyDataSetChanged();
    }
}

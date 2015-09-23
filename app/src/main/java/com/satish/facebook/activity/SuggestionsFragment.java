package com.satish.facebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.satish.facebook.R;
import com.satish.facebook.adapters.FindFriendAdapter;
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
public class SuggestionsFragment extends Fragment {
    private ListView listView;
    private FindFriendAdapter friendCustomAdapter;
    private ArrayList<Friend> friendArrayList;
    private static String tag = "json_tag";
    private static final String TAG = SuggestionsFragment.class.getSimpleName();
    private ProgressBar progressBar;
    private String id;
    private SQLiteHandler db;
    private TextView lbl_no_suggestions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggestions, container, false);
        listView = (ListView) view.findViewById(R.id.friend_list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lbl_no_suggestions= (TextView) view.findViewById(R.id.lbl_no_suggestion);
        friendArrayList = new ArrayList<>();
        db = new SQLiteHandler(getActivity());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getActivity(),
                        FriendProfileActivity.class);
                startActivity(in);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");
        String url = AppConfig.URL_FRIEND_SUGGESTIONS;
        url += "?id=" + id;
        friendCustomAdapter = new FindFriendAdapter(friendArrayList, getActivity(), id);
        listView.setAdapter(friendCustomAdapter);
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
                                String id = friendObj.getString("user_id");
                                String username = friendObj.getString("username");
                                String image = friendObj.getString("profile_image");
                                String name = toTitleCase(username);
                                Log.d(TAG, id + name);
                                Log.d("image", image);
                                Friend friend = new Friend();
                                friend.setId(id);
                                friend.setName(name);
                                friend.setProfileImageUrl(image);
                                friendArrayList.add(friend);
                            }

                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        } if(friendArrayList.size()>0) {
                            friendCustomAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            lbl_no_suggestions.setVisibility(View.VISIBLE);
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

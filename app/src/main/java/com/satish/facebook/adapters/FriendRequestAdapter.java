package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.satish.facebook.models.Friend;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by satish on 21/9/15.
 */
public class FriendRequestAdapter extends BaseAdapter {
    private ArrayList<Friend> friendArrayList;
    private LayoutInflater inflater;
    private Activity activity;
    private String id;
    private static String tag = "json_tag";
private  LinearLayout confirm_delete_layout;
    private TextView lblNowFriends;
    private   TextView lbl_name;
    public FriendRequestAdapter(ArrayList<Friend> friendArrayList, Activity activity, String id) {
        this.friendArrayList = friendArrayList;
        this.activity = activity;
        this.id = id;
    }

    @Override
    public int getCount() {
        return friendArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            view = inflater.inflate(R.layout.friends_request_list_view, null);
        lbl_name = (TextView) view.findViewById(R.id.friend_name);
        confirm_delete_layout= (LinearLayout) view.findViewById(R.id.confirm_delete_layout);
        lblNowFriends= (TextView) view.findViewById(R.id.lbl_your_now_friends);
        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button btnDelete = (Button) view.findViewById(R.id.btn_delete);
        NetworkImageView networkImage = (NetworkImageView) view.findViewById(R.id.friend_profile_image);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        final Friend friend = friendArrayList.get(position);
        lbl_name.setText(friend.getName());
        networkImage.setImageUrl(friend.getProfileImageUrl(), imageLoader);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity.getApplicationContext(), id + " " + friend.getId(), Toast.LENGTH_LONG).show();
                friendConfirm(id, friend.getId());
            }
        });
        return view;
    }

    private void friendConfirm(final String user_id, final String friend_id) {
        String url = AppConfig.URL_FRIEND_REQUEST_ACCEPT;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                lblNowFriends.setVisibility(View.VISIBLE);
                                confirm_delete_layout.setVisibility(View.GONE);
                            } else {
                            }
                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("FindFriendAdapter", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("friend_id", friend_id);
                return params;
            }
        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }
}

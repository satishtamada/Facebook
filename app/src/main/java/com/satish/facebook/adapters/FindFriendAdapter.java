package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.satish.facebook.R;
import com.satish.facebook.activity.SuggestionsFragment;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by satish on 30/8/15.
 */
public class FindFriendAdapter extends BaseAdapter {
    private static final String TAG = FindFriendAdapter.class.getSimpleName();
    private ArrayList<Friend> friendArrayList;
    private LayoutInflater inflater;
    private Activity activity;
    String id;
    private TextView lblRequestSent;
    private Button btnAddFriend;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private RequestSentAdapterListener requestSentAdapterListener;

    public void setRequestSentAdapterListener(SuggestionsFragment requestSentAdapterListener) {
        this.requestSentAdapterListener = requestSentAdapterListener;
    }

    public FindFriendAdapter(ArrayList<Friend> friendArrayList, Activity activity, String id) {

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            view = inflater.inflate(R.layout.find_friend_list_view, null);
        TextView lblName = (TextView) view.findViewById(R.id.friend_name);
        lblRequestSent = (TextView) view.findViewById(R.id.text_friend_request_sent);
        NetworkImageView profileImage = (NetworkImageView) view.findViewById(R.id.image);
        btnAddFriend = (Button) view.findViewById(R.id.add_friend);
        final Friend friend = friendArrayList.get(position);
        Log.d("id,name", friend.getName());
        lblName.setText(friend.getName());
        profileImage.setImageUrl(friend.getProfileImageUrl(), imageLoader);
        if (friend.getStatus() == AppConfig.REQUEST_STATUS_CONFIRMED) {
            lblRequestSent.setText("Request is sent");
            btnAddFriend.setVisibility(View.GONE);
        }
        else {
            lblRequestSent.setText("");
            btnAddFriend.setVisibility(View.VISIBLE);
        }
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAddFriend.getText().equals("Add Friend")) {
                    requestSent(position, id, friend.getId());
                }
            }
        });
        return view;
    }

    private void requestSent(final int position, final String user_id, final String friend_id) {
        String tag_string_req = "req_friend_confirm";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FRIEND_ADD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("FriendRequestAdapter", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        requestSentAdapterListener.onRequestSentConfirmed(position);
                    } else {
                      Toast.makeText(activity.getApplicationContext(),"Failed to add friend",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to add friend url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("friend_id", friend_id);

                Log.e(TAG, "params: " + params.toString());

                return params;
            }
        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public interface RequestSentAdapterListener {
        void onRequestSentConfirmed(int position);

    }

}
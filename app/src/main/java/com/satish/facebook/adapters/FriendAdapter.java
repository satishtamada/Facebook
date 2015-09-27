package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import com.satish.facebook.activity.FriendsListFragment;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Friend;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by satish on 18/8/15.
 */
public class FriendAdapter extends BaseAdapter {
    private static final String TAG = FriendAdapter.class.getSimpleName();
    String id;
    private ArrayList<Friend> friendArrayList;
    private LayoutInflater inflater;
    private Activity activity;
    private RemoveFriendAdapterListener removeFriendAdapterListener;

    public FriendAdapter(ArrayList<Friend> friendArrayList, Activity activity, String id) {

        this.friendArrayList = friendArrayList;
        this.activity = activity;
        this.id = id;
    }

    public void setRemoveFriendAdapterListener(FriendsListFragment removeFriendAdapterListener) {
        this.removeFriendAdapterListener = removeFriendAdapterListener;
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
            view = inflater.inflate(R.layout.friends_list_view, null);
        TextView lbl_name = (TextView) view.findViewById(R.id.friend_name);
        NetworkImageView networkImage = (NetworkImageView) view.findViewById(R.id.friend_profile_image);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        final Button btnFriend = (Button) view.findViewById(R.id.un_friend);
        final TextView lblFriendRemoved = (TextView) view.findViewById(R.id.txt_friend_removed);
        final Friend friend = friendArrayList.get(position);
        final String name = friend.getName();
        Log.d("id,name", friend.getName());
        lbl_name.setText(name);
        networkImage.setImageUrl(friend.getProfileImageUrl(), imageLoader);
        if (friend.getStatus() == AppConfig.FRIEND_STATUS_REMOVED) {
            lblFriendRemoved.setText("Friend removed");
            btnFriend.setVisibility(View.GONE);
        } else {
            lblFriendRemoved.setText("");
            btnFriend.setVisibility(View.VISIBLE);
        }
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFriend.getText().equals("Remove")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                    // Setting Dialog Message
                    alertDialog.setMessage(activity.getApplicationContext().getString(R.string.msg_prompt_remove_friend).replace("#name#", name));
                    alertDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeFriend(position, id, friend.getId());
                        }
                    });

                    alertDialog.show();
                    //event button request add friend
                }
            }
        });

        return view;
    }

    private void removeFriend(final int position, final String user_id, final String friend_id) {
        String tag_string_req = "req_friend_confirm";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REMOVE_FRIEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("FriendRequestAdapter", "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        removeFriendAdapterListener.onRemoveFriend(position);
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "Failed to add friend", Toast.LENGTH_SHORT).show();
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


    public interface RemoveFriendAdapterListener {
        void onRemoveFriend(int position);
    }
}

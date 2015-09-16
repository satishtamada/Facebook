package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Friend;

import java.util.ArrayList;

/**
 * Created by satish on 18/8/15.
 */
public class FriendAdapter extends BaseAdapter {
    private ArrayList<Friend> friendArrayList;
    private LayoutInflater inflater;
    private Activity activity;

    public FriendAdapter(ArrayList<Friend> friendArrayList, Activity activity) {

        this.friendArrayList = friendArrayList;
        this.activity = activity;
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
            view = inflater.inflate(R.layout.friends_list_view, null);
        TextView lbl_name = (TextView) view.findViewById(R.id.friend_name);
        NetworkImageView networkImage = (NetworkImageView) view.findViewById(R.id.image);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        final Button btnFriend = (Button) view.findViewById(R.id.un_friend);
        final TextView lblRequestSent = (TextView) view.findViewById(R.id.text_friend_request_sent);
        final Friend friend = friendArrayList.get(position);
        final String name = friend.getName();
        Log.d("id,name", friend.getName());
        lbl_name.setText(name);
        networkImage.setImageUrl(friend.getProfileImageUrl(), imageLoader);
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFriend.getText().equals("Friends")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                    // Setting Dialog Message
                    alertDialog.setMessage(activity.getApplicationContext().getString(R.string.msg_prompt_remove_friend).replace("#name#", name));
                    alertDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            btnFriend.setTextColor(Color.parseColor("#4e5665"));
                            btnFriend.setText("Add Friend");
                            lblRequestSent.setText("Friend removed");
                        }
                    });

                    // Setting Negative "NO" Button

                    alertDialog.show();
                    //event button request add friend
                } else if (btnFriend.getText().equals("Add Friend")) {
                    lblRequestSent.setText("Request is sent");
                    btnFriend.setText("Cancel");
                    btnFriend.setTextColor(Color.parseColor("#00b4e2"));
                    //event button cancel request
                } else if (btnFriend.getText().equals("Cancel")) {
                    lblRequestSent.setText("Request canceled");
                    btnFriend.setText("Add Friend");
                    btnFriend.setTextColor(Color.parseColor("#4e5665"));
                } else {
                }
            }
        });

        return view;
    }
}

package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Friend;

import java.util.ArrayList;

/**
 * Created by satish on 30/8/15.
 */
public class FindFriendAdapter extends BaseAdapter {
    private ArrayList<Friend> friendArrayList;
    private LayoutInflater inflater;
    private Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FindFriendAdapter(ArrayList<Friend> friendArrayList, Activity activity) {

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
            view = inflater.inflate(R.layout.find_friend_list_view, null);
        TextView lblName = (TextView) view.findViewById(R.id.friend_name);
        final TextView lblRequestSent = (TextView) view.findViewById(R.id.text_friend_request_sent);
        NetworkImageView profileImage = (NetworkImageView) view.findViewById(R.id.image);
        final ToggleButton btToggleAddFriend = (ToggleButton) view.findViewById(R.id.add_friend);
        final Friend friend = friendArrayList.get(position);
        Log.d("id,name", friend.getName());
        lblName.setText(friend.getName());
        profileImage.setImageUrl(friend.getProfileImageUrl(), imageLoader);
        btToggleAddFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i("info", "Button2 is on!");
                    lblRequestSent.setText("Request is sent");
                    btToggleAddFriend.setTextColor(Color.parseColor("#00b4e2"));
                    Toast.makeText(activity, friend.getId(), Toast.LENGTH_LONG).show();
                } else {
                    Log.i("info", "Button2 is off!");
                    lblRequestSent.setText("");
                    btToggleAddFriend.setTextColor(Color.parseColor("#4e5665"));
                }
            }
        });
        return view;
    }
}
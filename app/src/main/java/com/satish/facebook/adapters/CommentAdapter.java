package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Comments;

import java.util.ArrayList;

/**
 * Created by satish on 2/9/15.
 */
public class CommentAdapter extends BaseAdapter {
    private ArrayList<Comments> commetArrayList;
    private LayoutInflater inflater;
    private Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CommentAdapter(ArrayList<Comments> commetArrayList, Activity activity) {
        this.commetArrayList = commetArrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
       return commetArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return commetArrayList.get(position);
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
            view = inflater.inflate(R.layout.activity_commet_list_item_view, null);
        TextView lblName = (TextView) view.findViewById(R.id.username);
        NetworkImageView profileImage = (NetworkImageView) view.findViewById(R.id.profile_image);
        TextView lblComment = (TextView) view.findViewById(R.id.comment);
        TextView lblCreated_at = (TextView) view.findViewById(R.id.created_at);
        Comments c=commetArrayList.get(position);
        lblName.setText(c.getCommented_username());
        lblComment.setText(c.getComment());
        lblCreated_at.setText(c.getCreated_at().toString());
        profileImage.setImageUrl(c.getProfile_image(),imageLoader);
        return view;
    }
}

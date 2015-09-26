package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by satish on 2/9/15.
 */
public class CommentAdapter extends BaseAdapter {
    private ArrayList<Comments> commnetArrayList;
    private LayoutInflater inflater;
    private Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public CommentAdapter(ArrayList<Comments> commentArrayList, Activity activity) {
        this.commnetArrayList = commentArrayList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return commnetArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return commnetArrayList.get(position);
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
            view = inflater.inflate(R.layout.activity_commet_list_item_view, null);
        TextView lblName = (TextView) view.findViewById(R.id.username);
        NetworkImageView profileImage = (NetworkImageView) view.findViewById(R.id.profile_image);
        TextView lblComment = (TextView) view.findViewById(R.id.comment);
        TextView lblCreated_at = (TextView) view.findViewById(R.id.created_at);
        Comments c = commnetArrayList.get(position);
        lblName.setText(c.getCommented_username());
        profileImage.setImageUrl(c.getProfile_image(), imageLoader);
        lblComment.setText(c.getComment());
        //setting time to created at
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(c.getCreated_at().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(calendar.getTimeInMillis())),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        lblCreated_at.setText(timeAgo);
        return view;

    }
}

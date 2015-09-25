package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
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
import com.satish.facebook.models.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by satish on 24/9/15.
 */
public class NotificationsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private ArrayList<Notifications> notificationsArrayList;

    public NotificationsAdapter(ArrayList<Notifications> notificationsArrayList, Activity activity) {
        this.notificationsArrayList = notificationsArrayList;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return notificationsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationsArrayList.get(position);
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
            view = inflater.inflate(R.layout.notifications_list_view, null);
        NetworkImageView profileImage = (NetworkImageView) view.findViewById(R.id.profile_image);
        TextView lblMessage = (TextView) view.findViewById(R.id.message);
        TextView lblCreated_at = (TextView) view.findViewById(R.id.created_at);
        Notifications notifications = notificationsArrayList.get(position);
        profileImage.setImageUrl(notifications.getProfile_image(), imageLoader);
        lblMessage.setText(Html.fromHtml(notifications.getMessage()));
        //setting time to created at
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(notifications.getCreated_at().toString());
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

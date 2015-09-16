package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.activity.CommentActivity;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.FeedImageView;
import com.satish.facebook.models.Feed;

import java.util.List;


/**
 * Created by satish on 24/8/15.
 */
public class FeedAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<Feed> feedList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedAdapter(Activity activity, List<Feed> feedList) {
        this.activity = activity;
        this.feedList = feedList;
    }

    @Override

    public int getCount() {
        return feedList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);
        final LinearLayout like= (LinearLayout) convertView.findViewById(R.id.like_layout);
        LinearLayout comment= (LinearLayout) convertView.findViewById(R.id.comment_layout);
        final ImageView like_icon= (ImageView) convertView.findViewById(R.id.icon_like);
        final TextView lblLike= (TextView) convertView.findViewById(R.id.lbl_like);
        final Feed item = feedList.get(position);

        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getCreated_at()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfileImageUrl(), imageLoader);
        // Feed image
        if (item.getImage() != null) {
            feedImageView.setImageUrl(item.getImage(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lblLike.getCurrentTextColor()== Color.parseColor("#9197a3")){
                   lblLike.setTextColor(Color.parseColor("#00a0b4"));
                    like_icon.setImageResource(R.drawable.ic_like_select);
                }
                else {
                    lblLike.setTextColor(Color.parseColor("#9197a3"));
                    like_icon.setImageResource(R.drawable.ic_like_unselect);
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent=new Intent(activity, CommentActivity.class);
                commentIntent.putExtra("post_id",item.getPost_id());
                activity.startActivity(commentIntent);
            }
        });

        return convertView;
    }
}

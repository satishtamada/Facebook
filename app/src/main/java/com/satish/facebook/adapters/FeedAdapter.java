package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.satish.facebook.R;
import com.satish.facebook.activity.CommentActivity;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.FeedImageView;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.models.Feed;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by satish on 24/8/15.
 */
public class FeedAdapter extends BaseAdapter {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LayoutInflater inflater;
    private Activity activity;
    private List<Feed> feedList;
    private SQLiteHandler db;
    private static final String TAG = FeedAdapter.class.getSimpleName();

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
        db = new SQLiteHandler(activity);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView lblCommentsCount = (TextView) convertView.findViewById(R.id.comments_count);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);
        final LinearLayout like = (LinearLayout) convertView.findViewById(R.id.like_layout);
        LinearLayout comment = (LinearLayout) convertView.findViewById(R.id.comment_layout);
        final ImageView like_icon = (ImageView) convertView.findViewById(R.id.icon_like);
        final TextView lblLike = (TextView) convertView.findViewById(R.id.lbl_like);
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
        if (item.getLike_status() == 1) {
            lblLike.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            like_icon.setImageResource(R.drawable.ic_like_selected);
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lblLike.getCurrentTextColor() == Color.parseColor("#9197a3")) {
                    HashMap<String, String> user = db.getUserDetails();
                    String userId = user.get("uid");
                    String postId = Integer.toString(item.getPost_id());
                    String status = "1";
                    likeOnPost(userId, postId, status);
                    lblLike.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                    like_icon.setImageResource(R.drawable.ic_like_selected);
                } else {
                    HashMap<String, String> user = db.getUserDetails();
                    String userId = user.get("uid");
                    String postId = Integer.toString(item.getPost_id());
                    removeLikeOnPost(userId,postId);
                    lblLike.setTextColor(Color.parseColor("#9197a3"));
                    like_icon.setImageResource(R.drawable.ic_like_unselect);
                }
            }
        });
        lblCommentsCount.setText(Integer.toString(item.getComments_count()));
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(activity, CommentActivity.class);
                commentIntent.putExtra("post_id", item.getPost_id());
                activity.startActivity(commentIntent);
            }
        });
        return convertView;
    }

    private void removeLikeOnPost(final String userId, final String postId) { String tag_string_req = "like_on_post";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LIKE_REMOVE_ON_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "like Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(activity.getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to remove like url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("post_id", postId);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void likeOnPost(final String userId, final String postId, final String status) {
        String tag_string_req = "like_on_post";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LIKE_ON_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "like Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(activity.getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to comment_post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("post_id", postId);
                params.put("like_status", status);
                Log.d(TAG, userId + postId + status);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}

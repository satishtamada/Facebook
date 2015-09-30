package com.satish.facebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.satish.facebook.R;
import com.satish.facebook.adapters.NotificationsAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.models.Notifications;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by satish on 20/9/15.
 */
public class NotificationFragment extends Fragment {
    private static final String TAG = NotificationFragment.class.getSimpleName();
    private static String tag = "json_tag";
    String id;
    Timestamp timestamp;
    private ListView listView;
    private ArrayList<Notifications> notificationsArrayList;
    private NotificationsAdapter notificationsAdapter;
    private ProgressBar progressBar;
    private LinearLayout noNotificationsLayout;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        listView = (ListView) view.findViewById(R.id.notifications_list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noNotificationsLayout = (LinearLayout) view.findViewById(R.id.no_notifications_layout);
        db = new SQLiteHandler(getActivity());
        notificationsArrayList = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(notificationsArrayList, getActivity());
        listView.setAdapter(notificationsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Notifications notifications = notificationsArrayList.get(position);
                Intent i = new Intent(getActivity(), FeedItemActivity.class);
                i.putExtra("post_id", notifications.getPost_id());
                startActivity(i);

            }
        });
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");
        String url = AppConfig.URL_NOTIFICATIONS;
        url += "?user_id=" + id;
        Log.d(TAG, ""+id);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            boolean error = response.getBoolean("success");
                            if (error) {
                                Log.d(TAG, "hello");
                                JSONArray jsonArray = response.getJSONArray("notifications");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                    String id = friendObj.getString("user_id");
                                    String name = friendObj.getString("username");
                                    String profileImage = friendObj.getString("profile_image");
                                    String message = friendObj.getString("message");
                                    String created_at = friendObj.getString("created_at");
                                    int post_id = friendObj.getInt("post_id");
                                    String userName = toTitleCase(name);
                                    try {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                                        Date parsedDate = dateFormat.parse(created_at);
                                        timestamp = new java.sql.Timestamp(parsedDate.getTime());
                                    } catch (Exception e) {
                                    }
                                    Notifications notification = new Notifications();
                                    notification.setUser_id(Integer.parseInt(id));
                                    notification.setUser_name(userName);
                                    notification.setProfile_image(profileImage);
                                    notification.setMessage(message);
                                    notification.setCreated_at(timestamp);
                                    notificationsArrayList.add(notification);
                                    notification.setPost_id(post_id);
                                }
                            } else {
                                listView.setVisibility(View.GONE);
                                noNotificationsLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                        if (notificationsArrayList.size() > 0) {
                            notificationsAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            noNotificationsLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                progressBar.setVisibility(View.GONE);
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);
        return view;
    }

    private String toTitleCase(String name) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : name.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }
}
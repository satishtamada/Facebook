package com.satish.facebook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.satish.facebook.R;
import com.satish.facebook.adapters.CommentAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.models.Comments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by satish on 2/9/15.
 */
public class CommentActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Comments> commetArrayList;
    private static final String TAG = CommentActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private static String tag = "json_tag";
    private CommentAdapter commentAdapter;
    private EditText txtComment;
    private Button btnComment;
    private ImageView noCommentIcon;
    private TextView lblNoComments;    private ProgressBar progressBar;
    int post_id;
    private SQLiteHandler db;
    Timestamp timestamp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_layout);
        listView = (ListView) findViewById(R.id.comment_list_view);
        btnComment = (Button) findViewById(R.id.btn_comment_post);
        txtComment = (EditText) findViewById(R.id.txt_comment);
        noCommentIcon = (ImageView) findViewById(R.id.no_comments);
        lblNoComments = (TextView) findViewById(R.id.lbl_no_comments); progressBar = (ProgressBar) findViewById(R.id.progressBar);
        commetArrayList = new ArrayList<>();
        db = new SQLiteHandler(this);
        commentAdapter = new CommentAdapter(commetArrayList, this);
        listView.setAdapter(commentAdapter);

        Intent i = getIntent();
        //post id
        post_id = i.getIntExtra("post_id", 0);

        txtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_right_arrow_select));
                if (s.toString().trim().length() == 0)
                    btnComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_right_arrow));
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user = db.getUserDetails();
                String comment = txtComment.getText().toString();
                String userId = user.get("uid");
                String postId = Integer.toString(post_id);
                commentPost(comment, userId, postId);
                txtComment.setText("");
                commentAdapter.notifyDataSetChanged();
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        String url = AppConfig.URL_COMMENTS;
        url += "?post_id=" + post_id;
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
                                JSONArray jsonArray = response.getJSONArray("comments");

                                Log.d(TAG, "size is" + jsonArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject friendObj = (JSONObject) jsonArray.get(i);
                                    String id = friendObj.getString("user_id");
                                    String name = friendObj.getString("username");
                                    String image = friendObj.getString("profile_image");
                                    String comment_text = friendObj.getString("comment");
                                    String created_at = friendObj.getString("created_at");
                                    String userName = toTitleCase(name);
                                    try {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                                        Date parsedDate = dateFormat.parse(created_at);
                                        timestamp = new java.sql.Timestamp(parsedDate.getTime());
                                    } catch (Exception e) {
                                    }
                                    Log.d(TAG, id + name);
                                    Log.d("image", image);
                                    Comments comment = new Comments();
                                    comment.setCommented_user_id(Integer.parseInt(id));
                                    comment.setCommented_username(userName);
                                    comment.setProfile_image(image);
                                    comment.setComment(comment_text);
                                    comment.setCreated_at(timestamp);
                                    commetArrayList.add(comment);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "no comments ", Toast.LENGTH_LONG).show();
                                noCommentIcon.setImageResource(R.drawable.ic_no_comments);
                                lblNoComments.setText("No Comments Yet");
                            }
                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                        commentAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                // params.put("tag", "register");
                //  params.put("post_id", "341");
                return params;
            }

        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);
    }
    private void commentPost(final String comment, final String userId, final String postId) {
        String tag_string_req = "comment_post";
        progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_POST_COMMENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Comment Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        JSONObject errorObj = jObj.getJSONObject("error");
                        String errorMsg = errorObj.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to comment_post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user1_id", userId);
                params.put("post_id", postId);
                params.put("comment", comment);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

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

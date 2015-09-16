package com.satish.facebook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.satish.facebook.R;
import com.satish.facebook.adapters.CommentAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Comments;

import org.json.JSONArray;
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
    private TextView lblNoComments;
    Timestamp timestamp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_layout);
        listView = (ListView) findViewById(R.id.comment_list_view);
        btnComment= (Button) findViewById(R.id.btn_comment_post);
        txtComment= (EditText) findViewById(R.id.txt_comment);
        noCommentIcon= (ImageView) findViewById(R.id.no_comments);
        lblNoComments= (TextView) findViewById(R.id.lbl_no_comments);
        commetArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commetArrayList, this);
        listView.setAdapter(commentAdapter);


        Intent i=getIntent();
        //post id
        int post_id=i.getIntExtra("post_id",0);
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
                if (s.length()==0)
                    btnComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_right_arrow));
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        String url = AppConfig.URL_POST_COMMENTS;
        url += "?post_id=" + post_id;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            boolean error = response.getBoolean("success");
                            if(error) {
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
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"no comments ",Toast.LENGTH_LONG).show();
                                noCommentIcon.setImageResource(R.drawable.ic_no_comments);
                                lblNoComments.setText("No Comments Yet");
                            }
                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                        commentAdapter.notifyDataSetChanged();
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
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

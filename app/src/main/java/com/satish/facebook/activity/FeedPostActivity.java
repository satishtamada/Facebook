package com.satish.facebook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.satish.facebook.R;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by satish on 26/8/15.
 */
public class FeedPostActivity extends AppCompatActivity {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Button btnPostImage;
    private Button btnPost;
    private EditText txtPostText;
    private TextView lblUserName;
    private ProgressDialog pDialog;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = FeedPostActivity.class.getSimpleName();
    private com.android.volley.toolbox.NetworkImageView userProfileImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text_post);
        txtPostText = (EditText) findViewById(R.id.text_post);
        btnPostImage = (Button) findViewById(R.id.btn_image);
        btnPost = (Button) findViewById(R.id.btn_post);
        lblUserName= (TextView) findViewById(R.id.username);
        Intent intent = getIntent();
        userProfileImage= (NetworkImageView) findViewById(R.id.user_profile_image);
        lblUserName= (TextView) findViewById(R.id.username);
        lblUserName.setText(intent.getStringExtra("userName"));
        userProfileImage.setImageUrl(intent.getStringExtra("userProfileImage"), imageLoader);
        pDialog = new ProgressDialog(this);
        //event on post button
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = txtPostText.getText().toString();
                String user_id = Integer.toString(61);
                //  String image = "";
                if (!postText.isEmpty()) {
                    userPost(postText, user_id);
                    Toast.makeText(getApplicationContext(), "successfully posted", Toast.LENGTH_LONG).show();
                }
            }
        });
        //event on button image post
        btnPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "select image", Toast.LENGTH_LONG).show();
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    private void userPost(final String postText, final String user_id) {

        String tag_string_req = "req_register";
        pDialog.setMessage("Posted on Facebook ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FEED_CREATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");

                    if (error) {
                        Toast.makeText(getApplicationContext(), "Register successfully", Toast.LENGTH_LONG).show();
                        hideDialog();
                        // inputEmail.setText("");
                        // JSONObject user = jObj.getJSONObject("profile");
                        // String name = user.getString("name");
                        // String email = user.getString("email");
                        // String uid = user.getString("apikey");
                        /// String created_at = user.getString("created_at");
                        //  String id = user.getString("id");
                        // Log.d("email and name is", name + "," + email + "," + uid + "," + created_at + "," + id);


                        // Launch login activity
                        //  Intent intent = new Intent(
                        //        RegisterActivity.this,
                        //        LoginActivity.class);
                        // startActivity(intent);
                        //  finish();
                    } else {

                        JSONObject errorObj = jObj.getJSONObject("error");
                        // String errorMsg = errorObj.getString("message");
                        // Toast.makeText(getApplicationContext(), "Register fails", Toast.LENGTH_LONG).show();
                        // hideDialog();

                        //  Toast.makeText(getApplicationContext(),
                        //         errorMsg, Toast.LENGTH_LONG).show();
                        // inputEmail.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Post Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                // params.put("tag", "register");
                params.put("user_id", user_id);
                params.put("text", postText);
                //  params.put("image",image);
                Log.d("in getParams", postText);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

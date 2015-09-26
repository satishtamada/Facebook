package com.satish.facebook.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SQLiteHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by satish on 26/8/15.
 */
public class FeedCreateActivity extends AppCompatActivity {
    private Button btnPostImage;
    private EditText txtPostText;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = FeedCreateActivity.class.getSimpleName();
    private ImageView imgUploadFeed;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    Bitmap image;
    String responseString = null;
    String postText;
    String id;
    private SQLiteHandler db;
    byte[] bytes;
    ByteArrayOutputStream byteArrayOutputStream = null;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private NetworkImageView userProfileImage;
    String profileImageUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_post);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post to Facebook");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtPostText = (EditText) findViewById(R.id.feed_text_post);
        btnPostImage = (Button) findViewById(R.id.btn_postImage);
        imgUploadFeed = (ImageView) findViewById(R.id.imageUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userProfileImage = (NetworkImageView) findViewById(R.id.user_profile_image);
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
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");
        profileImageUrl=user.get("profile_image");
        userProfileImage.setImageUrl(profileImageUrl, imageLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.post) {
            postText = txtPostText.getText().toString();
            if (!postText.isEmpty() || image != null) {

                new UploadFileToServer().execute();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String s = getRealPathFromURI(selectedImageUri);
            image = BitmapFactory.decodeFile(s);
            imgUploadFeed.setImageBitmap(image);
            imgUploadFeed.setAlpha((float) 0.4);
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }  //upload image to server

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(AppConfig.URL_FEED_CREATE);
            try {
                MultipartEntityBuilder multipartEntity =
                        MultipartEntityBuilder.create();
                multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                if (image != null) {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    bytes = byteArrayOutputStream.toByteArray();
                    ByteArrayBody bab = new ByteArrayBody(bytes, "forest.jpg");
                    multipartEntity.addPart("image", bab);
                }
                multipartEntity.addTextBody("user_id", id);
                multipartEntity.addTextBody("text", postText);
                httppost.setEntity(multipartEntity.build());
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                Log.d("response is", response.toString());
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        boolean error = jsonObject.getBoolean("success");
                        if (error) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                            progressBar.setVisibility(View.GONE);
                        } else
                            Toast.makeText(getApplicationContext(), "server busy...!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("profile", "Response from server: " + result);
            // showing the server response in an alert dialog
            // showAlert(result);
            super.onPostExecute(result);
        }

    }
}

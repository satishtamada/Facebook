package com.satish.facebook.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.satish.facebook.R;
import com.satish.facebook.app.AppConfig;
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
 * Created by satish on 18/9/15.
 */
public class ProfileImageActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    final int CROP_PIC = 2;
    final int CAMERA_CAPTURE = 1;
    Bitmap image;
    byte[] bytes;
    String id;
    ByteArrayOutputStream byteArrayOutputStream = null;
    String responseString = null;
    private Toolbar toolbar;
    private Button btnUploadImage;
    private ImageView imgUploadImage;
    private Uri picUri;
    private ProgressBar progressBar;
    private SQLiteHandler db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_upload);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnUploadImage = (Button) findViewById(R.id.btn_upload_profile);
        imgUploadImage = (ImageView) findViewById(R.id.image_upload_profile);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a Picture");
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");
        //event on button upload profile
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image != null) {
                    new UploadFileToServer().execute();

                } else {
                    //fetch image from sd card
                    Intent captureIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //crop image
                    startActivityForResult(captureIntent, RESULT_LOAD_IMAGE);
                }
            }
        });
        //event on image upload profile
        imgUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                // get the Uri for the captured image
                picUri = data.getData();
                performCrop();
            }
            // user is returning from cropping the image
            else if (requestCode == CROP_PIC) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                image = extras.getParcelable("data");
                byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
                imgUploadImage.setImageBitmap(image);
                imgUploadImage.setAlpha((float) 1.0);
                btnUploadImage.setText("Set as Profile Picture");

            }
        }
    }

    //setting skip button to menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.skip) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //peform select image to crop
    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    //upload image to server
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(AppConfig.URL_PROFILE_UPLOAD);
            try {
                MultipartEntityBuilder multipartEntity =
                        MultipartEntityBuilder.create();
                multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                ByteArrayBody bab = new ByteArrayBody(bytes, "forest.jpg");
                multipartEntity.addPart("profile_image", bab);
                multipartEntity.addTextBody("user_id", id);
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

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
import android.widget.Toast;

import com.satish.facebook.R;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.helper.AndroidMultiPartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by satish on 18/9/15.
 */
public class ProfileImageActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button btnUploadImage;
    private ImageView imgUploadImage;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri picUri;
    final int CROP_PIC = 2;
    final int CAMERA_CAPTURE = 1;
    long totalSize = 0;Bitmap image;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_upload);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnUploadImage = (Button) findViewById(R.id.btn_upload_profile);
        imgUploadImage = (ImageView) findViewById(R.id.image_upload_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a Picture");

        //event on button upload profile
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(captureIntent, RESULT_LOAD_IMAGE);
                new UploadFileToServer().execute();
            }
        });
        //event on image upload profile
        imgUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


    } private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
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
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(AppConfig.URL_PROFILE_UPLOAD);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity( new AndroidMultiPartEntity.ProgressListener() {

                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                // Adding file data to http body
                entity.addPart("profile_image",new FileBody(new File(image.toString())));
                // Extra parameters user_id to server
                entity.addPart("user_id", new StringBody("121"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
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
                imgUploadImage.setImageBitmap(image);
                imgUploadImage.setAlpha((float) 1.0);
                btnUploadImage.setText("Set as Profile Picture");

            }
    }
    }

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
        }
        catch (ActivityNotFoundException anfe) {
          Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

}

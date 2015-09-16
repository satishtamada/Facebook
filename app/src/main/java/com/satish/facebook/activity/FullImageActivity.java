package com.satish.facebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.app.AppController;


/**
 * Created by satish on 2/9/15.
 */
public class FullImageActivity extends AppCompatActivity {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        // get intent data
        Intent i = getIntent();
        // Selected image id
        final int position = i.getExtras().getInt("id");
        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.full_image_view);
        Button btnComment= (Button) findViewById(R.id.btnComment);
        imageView.setImageUrl(ImagesActivity.imageUrls.get(position), imageLoader);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(getApplicationContext(), CommentActivity.class);
                commentIntent.putExtra("post_id",Integer.parseInt(ImagesActivity.postIds.get(position)));
                startActivity(commentIntent);
            }
        });

    }

}

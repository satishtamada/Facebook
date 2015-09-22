package com.satish.facebook.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.satish.facebook.R;
import com.satish.facebook.adapters.ImagesAdapter;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.helper.Utils;

import java.util.ArrayList;

/**
 * Created by satish on 31/8/15.
 */
public class ImagesActivity extends AppCompatActivity {
    public static ArrayList<String> imageUrls;
    public static ArrayList<String> postIds;
    private Utils utils;
    private Toolbar toolbar;
    private GridView gridView;
    private int columnWidth;
    private ImagesAdapter imagesAdapter;
    private String userName;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        gridView = (GridView) findViewById(R.id.grid_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setVisibility(View.VISIBLE);
        utils = new Utils(this);

        Intent imagesIntent = getIntent();
        imageUrls = imagesIntent.getStringArrayListExtra("postedImages");
        postIds = imagesIntent.getStringArrayListExtra("postImagesIds");
        userName = imagesIntent.getStringExtra("userName");
        getSupportActionBar().setTitle(userName);

        // Initilizing Grid View
        InitilizeGridLayout();
        Log.d("postIds", Integer.toString(postIds.size()));
        imagesAdapter = new ImagesAdapter(ImagesActivity.this, imageUrls,
                columnWidth);
        // Instance of ImageAdapter Class
        gridView.setAdapter(imagesAdapter);
        progressBar.setVisibility(View.GONE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                // passing array index
                i.putExtra("id", position);
                startActivity(i);
            }
        });
    }


    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConfig.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((AppConfig.NUM_OF_COLUMNS + 1) * padding)) / AppConfig.NUM_OF_COLUMNS);
        gridView.setNumColumns(AppConfig.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }
}
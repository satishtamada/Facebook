package com.satish.facebook.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.app.AppController;

import java.util.ArrayList;


/**
 * Created by satish on 31/8/15.
 */
public class ImagesAdapter extends BaseAdapter {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int imageWidth;
    private Activity activity;
    private ArrayList<String> imageUrls = new ArrayList<String>();

    public ImagesAdapter(Activity activity, ArrayList<String> filePaths,
                         int imageWidth) {
        this.activity = activity;
        this.imageUrls = filePaths;
        this.imageWidth = imageWidth;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetworkImageView imageView = new NetworkImageView(activity);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
        imageView.setImageUrl(imageUrls.get(position), imageLoader);
        return imageView;
    }
}

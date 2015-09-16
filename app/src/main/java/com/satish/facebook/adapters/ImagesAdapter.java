package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.activity.FullImageActivity;
import com.satish.facebook.app.AppController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * Created by satish on 31/8/15.
 */
public class ImagesAdapter extends BaseAdapter {
    private int imageWidth;
    private Activity activity;
    private ArrayList<String> imageUrls = new ArrayList<String>();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

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
        NetworkImageView imageView;
        if (convertView == null) {
            imageView =new NetworkImageView(activity);
        } else {
            imageView = (NetworkImageView) convertView;
        }
        Bitmap image = decodeFile(imageUrls.get(position), imageWidth,
                imageWidth);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        imageView.setImageBitmap(image);
        imageView.setOnClickListener(new OnImageClickListener(position));
        imageView.setImageUrl(imageUrls.get(position), imageLoader);
        return imageView;

    }
    class OnImageClickListener implements View.OnClickListener {

        int position;
        // constructor
        public OnImageClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(activity, FullImageActivity.class);
            i.putExtra("position", position);
            activity.startActivity(i);
        }

    }
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

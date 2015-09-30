package com.satish.facebook.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.satish.facebook.app.AppConfig;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    private Context _context;

    // constructor
    public Utils(Context context) {
        this._context = context;
    }

    // Reading file paths from SDCard
    public ArrayList<String> getFilePaths() {
        ArrayList<String> filePaths = new ArrayList<String>();

        File directory = new File(
                android.os.Environment.getExternalStorageDirectory()
                        + File.separator + AppConfig.PHOTO_ALBUM);

        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();

            // Check for count
            if (listFiles.length > 0) {

                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {

                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();

                    // check for supported file extension
                    if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    }
                }
            } else {
                // image directory is empty
                Toast.makeText(
                        _context,
                        AppConfig.PHOTO_ALBUM
                                + " is empty. Please load some images in it !",
                        Toast.LENGTH_LONG).show();
            }

        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(_context);
            alert.setTitle("Error!");
            alert.setMessage(AppConfig.PHOTO_ALBUM
                    + " directory path is not valid! Please set the image directory name AppConstant.java class");
            alert.setPositiveButton("OK", null);
            alert.show();
        }

        return filePaths;
    }

    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (AppConfig.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }

    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public static String getTimeStamp(){
        String currrentTime; SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        currrentTime = (DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()).toString());

        Log.e(TAG, "currentTime: " + currrentTime);
        return currrentTime.toString();
    }

    public static boolean isFeedRequestTimeout(Context context){
        PrefManager pref = new PrefManager(context);
        String lastRequestTime = pref.getLastFeedRequestTime();

        if(lastRequestTime == null)
            return true;

        try {
            String currentTime = getTimeStamp();

            // check the time difference
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
            Date firstParsedDate = dateFormat.parse(lastRequestTime);
            Date secondParsedDate = dateFormat.parse(currentTime);
            long diff = (secondParsedDate.getTime() - firstParsedDate.getTime()) / (60 * 1000) % 60;

            Log.e(TAG, "Time diff: " + diff);

            if(diff > AppConfig.FEED_REQUEST_TIMEOUT){
                return true;
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }
}

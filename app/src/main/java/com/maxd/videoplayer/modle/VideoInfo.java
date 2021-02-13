package com.maxd.videoplayer.modle;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;

public class VideoInfo {
    private static final String[] mLocalVideoColumn = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA
    };

    private int id;
    private String display_name;
    private String title;
    private String mimeType;
    private long duration;
    private String data;

    public VideoInfo(int _id,String _display_name,String _title,String _mimeType,long _duration,String _data){
        id = _id;
        display_name = _display_name;
        title = _title;
        mimeType = _mimeType;
        duration = _duration;
        data = _data;
    }

    public VideoInfo(Cursor cursor){
        id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
    }

    public static String[] getLocalVideoColumn(){
        return mLocalVideoColumn;
    }
    public int getId(){
        return id;
    }

    public String getName(){
        return display_name;
    }

    public String getTitle(){
        return title;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getDuration(){
        return duration;
    }

    public String getStrDuration() {
        String duration_str;
        int total_sec = (int)duration/1000;
        int hour = total_sec / 3600;
        int min = (total_sec / 60) % 60;
        int sec = total_sec % 60;
        duration_str = String.format("%02d:%02d:%02d",hour,min,sec);
        return duration_str;
    }

    public String getData(){
        return data;
    }
}

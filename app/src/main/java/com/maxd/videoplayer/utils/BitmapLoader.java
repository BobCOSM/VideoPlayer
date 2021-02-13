package com.maxd.videoplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;

public class BitmapLoader {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    static public  Bitmap getVideoThumb(String videoPath,BitmapMemCache bmCache) throws IOException {
        Bitmap bitmap = bmCache.getBitmap(videoPath);
        if(bitmap == null){
            bitmap = ThumbnailUtils.createVideoThumbnail(new File(videoPath),new Size(64,64),null);
            bmCache.putBitmap(videoPath,bitmap);
        }
        return bitmap;
    }
}

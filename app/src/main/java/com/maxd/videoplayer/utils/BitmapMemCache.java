package com.maxd.videoplayer.utils;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class BitmapMemCache {

    //限定内存大小 防止使用过多内存
    private long memLimit = 4 * 1024 * 1024; //默认 4MB

    //当前使用内存大小
    private long memSize = 0;

    private Map<String, Bitmap> mCache = Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10,1.5f,false));

    public void setLimite(long limit){
        memLimit = limit;
    }

    public BitmapMemCache(){
        // 初始化 使用 当前线程的 1/8 内存;
        setLimite(Runtime.getRuntime().maxMemory() / 8);
    }

    private void checkLimit() {
        if(memSize > memLimit){

            Iterator<Map.Entry<String,Bitmap>> iterator = mCache.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,Bitmap> entry = iterator.next();
                iterator.remove();
                memSize -= getBitmapSize(entry.getValue());
                if(memSize < memLimit){
                    break;
                }
            }
        }
    }

    public void putBitmap(String path,Bitmap bitmap){
        mCache.put(path,bitmap);
        memSize += getBitmapSize(bitmap);
        checkLimit();
    }

    public Bitmap getBitmap(String path){
        Bitmap bitmap = null;
        bitmap = mCache.get(path);
        return bitmap;
    }

    private long getBitmapSize(Bitmap bitmap) {
        return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight();
    }
}

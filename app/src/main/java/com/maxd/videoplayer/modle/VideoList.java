package com.maxd.videoplayer.modle;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class VideoList {

    //存储视频信息
    private ArrayList<VideoInfo> videoListArray = new ArrayList<VideoInfo>();

    private Map<String,VideoInfo> videoInfoMap = Collections.synchronizedMap(new LinkedHashMap<String, VideoInfo>());

    private int mPlayIndex = -1;

    private VideoList(){
    }

    public static VideoList getInstance(){
        return VideoListHolder.videoListInstance;
    }
    public void putVideoInfo(VideoInfo videoInfo) {
        //videoInfoMap.put(videoInfo.getData(),videoInfo);
        synchronized (VideoList.class) {
            videoListArray.add(videoInfo);
        }
    }

    public VideoInfo getVideoInfo(int index){
        return videoListArray.get(index);
    }

    public ArrayList<VideoInfo> getVideoListArray(){
        return videoListArray;
    }

    public int size(){
        return videoListArray.size();
    }

    public int getPlayIndex(){
        return mPlayIndex;
    }

    public void setPlayIndex(int playIndex){
        mPlayIndex = playIndex;
    }

    private static class VideoListHolder{
        private static final VideoList videoListInstance = new VideoList();
    }
}

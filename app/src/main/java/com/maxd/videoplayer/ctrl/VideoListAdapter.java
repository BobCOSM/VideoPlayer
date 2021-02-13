package com.maxd.videoplayer.ctrl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.maxd.videoplayer.R;
import com.maxd.videoplayer.modle.VideoInfo;
import com.maxd.videoplayer.utils.BitmapLoader;
import com.maxd.videoplayer.utils.BitmapMemCache;

import java.io.IOException;
import java.util.ArrayList;

public class VideoListAdapter extends BaseAdapter {
    private static final String TAG = "VideoListAdapter";

    private ArrayList<VideoInfo> mVideoInfoList = null;
    private LayoutInflater mInflater = null;
    private Activity mContextActivity = null;

    private BitmapMemCache bmCache = null;


    private final static int MSG_VIDEOTHUMB_UPDATE = 0x01;

    private Handler mVideoAdapterHandler = new VideoListAdapterHandler();

    public VideoListAdapter(Activity activity,ArrayList videoInfoList,Handler handler){
        mContextActivity = activity;
        mInflater = (LayoutInflater) mContextActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mVideoInfoList = videoInfoList;

        bmCache = new BitmapMemCache();
    }

    @Override
    public int getCount() {
        return mVideoInfoList == null ? 0 : mVideoInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoInfoList == null ? null : mVideoInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mVideoInfoList == null ? 0 : position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoItemHolder holder = null;
        VideoInfo videoInfo = mVideoInfoList.get(position);
        if(convertView == null){
            holder = new VideoItemHolder();
            convertView = mInflater.inflate(R.layout.video_item_layout,null);
            holder.video_thumb = convertView.findViewById(R.id.video_thumb);
            holder.video_name = convertView.findViewById(R.id.video_name);
            holder.video_duration = convertView.findViewById(R.id.video_duration);

            convertView.setTag(holder);
            holder.video_thumb.setTag(videoInfo.getData());
        }
        else {
            holder = (VideoItemHolder) convertView.getTag();
        }

        if(videoInfo != null){
            holder.video_name.setText(videoInfo.getTitle());
            holder.video_duration.setText(videoInfo.getStrDuration());

            if( ! holder.video_thumb.getTag().equals(videoInfo.getData())){
                holder.video_thumb.setImageResource(R.mipmap.ic_launcher);
            }
            new LoadVideoThumbThread(videoInfo.getData(),holder.video_thumb).start();
        }
        return convertView;
    }

    private class VideoItemHolder{
        ImageView video_thumb;
        TextView video_name;
        TextView video_duration;
    }

    private class VideoListAdapterHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_VIDEOTHUMB_UPDATE:
                    VideoMsgData msgData = (VideoMsgData) msg.obj;
                    msgData.imageView.setImageBitmap(msgData.bitmap);
                    Log.d(TAG," ---------- VideoListAdapterHandler --> MSG_VIDEOTHUMB_UPDATE");
                    break;
                default:
                    break;
            }
        }
    }

    private class LoadVideoThumbThread extends Thread{

        private String videoPath = null;
        private ImageView imageView = null;
        public LoadVideoThumbThread(String path,ImageView view){
            videoPath = path;
            imageView = view;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            super.run();
            //获取缩略图
            Bitmap bitmap = null;
            try {
                bitmap = BitmapLoader.getVideoThumb(videoPath,bmCache);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //获取缩略图成功
            if(bitmap != null) {
                //发送消息到主线程 更新缩略图
                VideoMsgData msgData = new VideoMsgData();
                msgData.bitmap = bitmap;
                msgData.imageView = imageView;
                Message msg = mVideoAdapterHandler.obtainMessage(MSG_VIDEOTHUMB_UPDATE, msgData);
                mVideoAdapterHandler.sendMessage(msg);
            }
        }
    }

    public final class VideoMsgData{
        public ImageView imageView;
        public Bitmap bitmap;
    }
}

package com.zhangheng.mymusicplayer.dapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.bean.MusicBean;

import java.util.ArrayList;

/**
 * Created by zhangH on 2016/5/17.
 */
public class _MusicListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MusicBean> mMusicBeanArray;
    private int mPlaying = -1;

    public _MusicListAdapter(Context context, ArrayList<MusicBean> musicBeanArray) {
        mContext = context;
        mMusicBeanArray = musicBeanArray;
    }

    public void setPlaying(int index){
        mPlaying = index;
    }

    @Override
    public int getCount() {
        return mMusicBeanArray.size();
    }

    @Override
    public MusicBean getItem(int position) {
        return mMusicBeanArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_music_listview, null);
        }
        ViewHolder vh = ViewHolder.getViewHolder(convertView);
        MusicBean item = getItem(position);
        vh.mMusicName.setText(item.getMusicName());
        vh.mSinger.setText(item.getSinger());
        vh.mSpeakerIc.setVisibility(mPlaying == position ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }

    private static class ViewHolder {
        TextView mMusicName;
        TextView mSinger;
        ImageView mSpeakerIc;

        public ViewHolder(View view) {
            mMusicName = (TextView) view.findViewById(R.id.musicListItemMusicName_TextView);
            mSinger = (TextView) view.findViewById(R.id.musicListItemSinger_TextView);
            mSpeakerIc = (ImageView) view.findViewById(R.id.musicListItemSpeaker_ImageView);
        }

        public static ViewHolder getViewHolder(View view) {
            ViewHolder vh = (ViewHolder) view.getTag();
            if (vh == null) {
                vh = new ViewHolder(view);
                view.setTag(vh);
            }
            return vh;
        }
    }
}

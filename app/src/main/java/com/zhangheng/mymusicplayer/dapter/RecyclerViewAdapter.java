package com.zhangheng.mymusicplayer.dapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.bean.MusicBean;

import java.util.ArrayList;

/**
 * Created by zhangH on 2016/5/30.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<MusicBean> mMusicBeanArray;
    private int mPlaying;
    OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClickListener(View v, MusicBean music, int position);
    }

    public RecyclerViewAdapter(ArrayList<MusicBean> musicBeanArray) {
        mMusicBeanArray = musicBeanArray;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_listview, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.bindHolder(mMusicBeanArray.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mMusicBeanArray.size();
    }

    /** 根据position返回对应的MusicBean */
    public MusicBean getItem(int position) {
        return mMusicBeanArray.get(position);
    }

    /** 注册条目的点击事件的监听 */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setPlayedPosition(int position){
        mPlaying = position;
    }


    /** ViewHolder */
    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        View mItemView;
        TextView mMusicName;
        TextView mSinger;
        ImageView mSpeakerIc;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mMusicName = (TextView) itemView.findViewById(R.id.musicListItemMusicName_TextView);
            mSinger = (TextView) itemView.findViewById(R.id.musicListItemSinger_TextView);
            mSpeakerIc = (ImageView) itemView.findViewById(R.id.musicListItemSpeaker_ImageView);
        }

        public void bindHolder(final MusicBean musicBean, final int position) {
            mMusicName.setText(musicBean.getMusicName());
            mSinger.setText(musicBean.getSinger());
            mSpeakerIc.setVisibility(mPlaying == position ? View.VISIBLE : View.INVISIBLE);

            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnItemClickListener != null) {
                        /* 通知以前的条目刷新,去除播放状态icon */
                        notifyItemChanged(mPlaying);

                        /* 将播放状态icon改为当前点击的条目 */
                        mPlaying = position;
                        notifyItemChanged(position);

                        /* 通知观察者条目被点击 */
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClickListener(v, musicBean, position);
                    }
                }
            });

            mItemView.setTag(musicBean);
            Log.w(TAG, "bindHolder: getLayoutPosition: " + getLayoutPosition());
        }
    }
}

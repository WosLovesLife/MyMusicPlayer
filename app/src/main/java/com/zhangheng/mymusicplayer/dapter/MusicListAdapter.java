package com.zhangheng.mymusicplayer.dapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wosloveslife.baserecyclerview.adapter.BaseRecyclerViewAdapter;
import com.wosloveslife.baserecyclerview.viewHolder.BaseRecyclerViewHolder;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.bean.MusicBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhangh on 2016/8/6.
 */
public class MusicListAdapter extends BaseRecyclerViewAdapter<MusicBean> {
    private static final String TAG = "MusicListAdapter";

    private int mPlaying;

    @Override
    protected BaseRecyclerViewHolder<MusicBean> onCreateItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_listview, parent, false);
        return new RecyclerViewHolder(view);
    }

    /** 设置当前播放的条目,使播状态的标记得以变更 */
    public void setPlayedPosition(int position) {

        /* 通知以前的条目刷新,去除播放状态icon */
        notifyItemChanged(mPlaying);

        /* 将播放状态icon改为当前点击的条目 */
        mPlaying = position;

        /* 通知刷新当前的条目 */
        notifyItemChanged(position);
    }

    /** 根据position返回对应的MusicBean */
    public MusicBean getItem(int position) {
        return mData.get(position);
    }

    /** ViewHolder */
    class RecyclerViewHolder extends BaseRecyclerViewHolder<MusicBean> {
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

        @Override
        public void onBind(MusicBean data) {
            int index = mData.indexOf(data);

            mMusicName.setText(data.getMusicName());
            mSinger.setText(data.getSinger());
            mSpeakerIc.setVisibility(mPlaying == index ? View.VISIBLE : View.INVISIBLE);

            /* 条目被点击后同志监听者被电击的条目(MusicBean)和条目位置 */
            itemView.setOnClickListener(v -> EventBus.getDefault().post(new MusicItemClickedEvent(data,index)));

            mItemView.setTag(data);
            Log.w(TAG, "bindHolder: getLayoutPosition: " + getLayoutPosition());
        }
    }

    public class MusicItemClickedEvent{
        public MusicBean mMusicBean;
        public int mPosition;

        public MusicItemClickedEvent(MusicBean musicBean,int position){
            mMusicBean = musicBean;
            mPosition = position;
        }
    }
}

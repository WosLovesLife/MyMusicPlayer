package com.zhangheng.mymusicplayer.listener;

import com.zhangheng.mymusicplayer.bean.MusicBean;

import java.util.ArrayList;

/**
 * Created by zhangH on 2016/5/17.
 */
public interface OnMusicDispatchDataChangedListener {

    /** 当MusicDispatcher更新了数据集合时触发 */
    void onDispatchDataChanged(ArrayList<MusicBean> musicBeanArray, ArrayList<String> musicIndexArray, int currentIndex);

    void onItemChanged(int currentItem);
}

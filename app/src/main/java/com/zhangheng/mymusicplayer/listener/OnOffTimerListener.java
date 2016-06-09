package com.zhangheng.mymusicplayer.listener;

import java.io.Serializable;

/**
 * Created by zhangH on 2016/6/7.
 */
public interface OnOffTimerListener extends Serializable{
    void onOffTimer(long timerDate);
}

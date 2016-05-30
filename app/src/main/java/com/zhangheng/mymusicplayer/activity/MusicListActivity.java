package com.zhangheng.mymusicplayer.activity;


import android.app.Fragment;

import com.zhangheng.mymusicplayer.ui.MusicListFragment;

/**
 * Created by zhangH on 2016/5/17.
 */
public class MusicListActivity extends BaseActivity {

    @Override
    protected Fragment initComponentFragment() {
        return new MusicListFragment();
    }
}

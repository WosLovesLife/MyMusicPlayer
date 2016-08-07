package com.zhangheng.mymusicplayer.activity;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.zhangheng.mymusicplayer.fragment.MusicListFragment;

/**
 * Created by zhangH on 2016/5/17.
 */
public class MusicListActivity extends BaseActivity {

    private static final String TAG = "MusicListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected Fragment initFragment() {
        return new MusicListFragment();
    }

    /** 更改层级式导航(Toolbar左上角)的返回逻辑, 直接finish本页 */
    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Log.w(TAG, "getSupportParentActivityIntent: ");
        finish();
        return null;
    }
}

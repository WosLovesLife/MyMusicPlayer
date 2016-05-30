package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;

import com.zhangheng.mymusicplayer.ui.SplashFragment;

public class SplashActivity extends BaseActivity {

    @Override
    protected Fragment initComponentFragment() {
        return new SplashFragment();
    }
}

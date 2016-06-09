package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;

import com.zhangheng.mymusicplayer.fragment.SplashFragment;

public class SplashActivity extends BaseActivity {

    @Override
    protected Fragment initFragment() {
        return new SplashFragment();
    }
}

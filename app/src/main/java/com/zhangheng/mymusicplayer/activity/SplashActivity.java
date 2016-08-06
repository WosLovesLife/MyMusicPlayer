package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;

import com.project.myutilslibrary.ServiceStateUtils;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.engine.Controller;
import com.zhangheng.mymusicplayer.service.AudioPlayer;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected int inflateView() {
        return R.layout.fragment_splash;
    }

    @Override
    protected Fragment initFragment() {
        return null;
    }

    @Override
    protected void initData() {
        super.initData();

        if (ServiceStateUtils.isRunning(this, AudioPlayer.class)) {
            enterMainPage();
        } else {
            /** 这里调用该方法是为了在Splash页面是就开始加载服务 */
            Controller.newInstance(this);
            delayed();
        }
    }

    private void delayed() {

        new Handler().postDelayed(() -> {
            enterMainPage();

            /* 转场动画 */
            overridePendingTransition(R.anim.alpha_dialog_show, R.anim.alpha_dialog_hide);
        }, 500);
    }

    private void enterMainPage() {
        Intent i = new Intent(SplashActivity.this, MainPageActivity.class);
        startActivity(i);

        finish();
    }
}

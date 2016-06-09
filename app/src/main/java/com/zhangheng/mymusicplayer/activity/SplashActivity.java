package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.engine.Controller;
import com.zhangheng.mymusicplayer.fragment.SplashFragment;

public class SplashActivity extends BaseActivity {

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


        /** 这里调用该方法是为了在Splash页面是就开始加载服务 */
        Controller.newInstance(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterMainPage();
            }
        }, 500);
    }

    private void enterMainPage() {

        Intent i = new Intent(this, MainPageActivity.class);
        startActivity(i);

        finish();
        overridePendingTransition(R.anim.alpha_dialog_show, R.anim.alpha_dialog_hide);
    }

}

package com.zhangheng.mymusicplayer.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.myutilslibrary.ServiceStateUtils;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.activity.MainPageActivity;
import com.zhangheng.mymusicplayer.engine.Controller;
import com.zhangheng.mymusicplayer.service.AudioPlayer;

/**
 * Created by zhangH on 2016/5/17.
 */
public class SplashFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    protected void initData() {
        if (ServiceStateUtils.isRunning(getActivity(), AudioPlayer.class)) {
            enterMainPage();
        } else {
            /** 这里调用该方法是为了在Splash页面是就开始加载服务 */
            Controller.newInstance(getActivity());
            delayed();
        }
    }

    private void delayed() {

        new Handler().postDelayed(() -> {
            enterMainPage();

            /* 转场动画 */
            getActivity().overridePendingTransition(R.anim.alpha_dialog_show, R.anim.alpha_dialog_hide);
        }, 500);
    }

    private void enterMainPage() {
        Intent i = new Intent(getActivity(), MainPageActivity.class);
        startActivity(i);

        getActivity().finish();
    }
}

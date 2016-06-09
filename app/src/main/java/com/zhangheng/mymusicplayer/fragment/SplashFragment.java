package com.zhangheng.mymusicplayer.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.activity.MainPageActivity;

/**
 * Created by zhangH on 2016/5/17.
 */
public class SplashFragment extends Fragment{

    private ImageView mHorse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
//        mHorse = (ImageView) view.findViewById(R.id.splashHorse_ImageView);
//        runAnimRotate(mHorse);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enterMainPage();
    }

    private void runAnimRotate(View view) {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_splash_horse);
        rotate.setDuration(1314);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                enterMainPage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(rotate);
    }

    private void enterMainPage() {
        Intent i = new Intent(getActivity(),MainPageActivity.class);
        startActivity(i);
        getActivity().finish();
    }

}

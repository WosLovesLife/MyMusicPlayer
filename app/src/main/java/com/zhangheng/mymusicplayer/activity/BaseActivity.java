package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zhangheng.mymusicplayer.R;

/**
 * Created by zhangH on 2016/5/17.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = initView();
        if (layout > 0) {
            setContentView(layout);
        }else {
            setContentView(R.layout.activity_main_page);
        }

        initComponentView();
        initFragment();
        initData();
        setViewData();
    }

    protected int initView(){
        return -1;
    }

    /** 初始化子类自己的View */
    protected void initComponentView() {

    }

    private void initFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = initComponentFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }

    protected abstract Fragment initComponentFragment();

    protected void initData() {

    }

    protected void setViewData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

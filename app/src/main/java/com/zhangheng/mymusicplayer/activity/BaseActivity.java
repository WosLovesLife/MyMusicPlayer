package com.zhangheng.mymusicplayer.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.zhangheng.mymusicplayer.R;

/**
 * Created by zhangH on 2016/5/17.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        initView();
        bindToolbarAndDrawer();

        setFragment();
    }

    /** 将Fragment添加到默认容器中 */
    private void setFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = initFragment();
            if (fragment != null)
                fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    /** 将需要托管的Fragment通过该方法返回 */
    protected abstract Fragment initFragment();

    /** 设置Toolbar的Title和Subtitle */
    public static void setTitle(Activity activity, String title, String subTitle) {
        BaseActivity baseActivity = (BaseActivity) activity;
        if (baseActivity == null) return;

        ActionBar actionBar = baseActivity.getSupportActionBar();
        if (actionBar == null) return;

        if (!TextUtils.isEmpty(title))
            actionBar.setTitle(title);
        if (!TextUtils.isEmpty(subTitle))
            actionBar.setSubtitle(subTitle);
    }

    protected void initView(){
    };

    protected void bindToolbarAndDrawer() {
    }

    protected void initData() {

    }
}

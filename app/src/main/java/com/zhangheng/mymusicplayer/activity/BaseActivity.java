package com.zhangheng.mymusicplayer.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.zhangheng.mymusicplayer.R;

/**
 * Created by zhangH on 2016/5/17.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(inflateView());

//        ButterKnife.bind(this);

        setToolbar();

        initView();

        addFragment();

        initData();

        setViewData();
    }

    /** 如果子类重写了该方法,则将子类的布局作为默认布局, */
    protected int inflateView() {
        return R.layout.activity_base;
    }

    private void setToolbar() {
        /** 加载Toolbar,设置为应用的Actionb */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);

        bindToolbarAndDrawer(toolbar);
    }

    /** 如果页面中包含DrawerLayout和Toolbar,则在该方法中绑定二者 */
    protected void bindToolbarAndDrawer(Toolbar toolbar) {
    }

    /** 如果有需要,可以在该方法中初始化控件 */
    protected void initView() {
    }

    /** 将Fragment添加到默认容器中 */
    private void addFragment() {
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

    /** 如果有需要,可以在该方法中执行加载数据等操作 */
    protected void initData() {
    }

    /** 如果有需要,可以在该方法中处理View的业务,如设置数据 */
    protected void setViewData() {
    }

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
}

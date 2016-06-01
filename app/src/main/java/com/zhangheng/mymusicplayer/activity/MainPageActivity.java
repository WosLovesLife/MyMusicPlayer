package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.project.myutilslibrary.Toaster;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.ui.MainPageFragment;

import java.lang.reflect.Field;

/**
 * Created by zhangH on 2016/4/30.
 */
public class MainPageActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainPageActivity";

    @Override
    protected int initView() {
        return R.layout.drawer_main;
    }

    @Override
    protected void initComponentView() {

        /** 加载Drawer对象,用于Toolbar确定和Drawer的位置关系 */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        /** 加载Toolbar,设置为应用的Actionb */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            sinkStatusBar(toolbar);
        }

        setSupportActionBar(toolbar);

        /** 设置Drawer和Toolbar的开启关系 */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        /** 通知系统同步关系 */
        toggle.syncState();

        /** 加载Drawer导航组件,注册事件监听 */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void sinkStatusBar(Toolbar toolbar) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        int statusBarHeight = 0;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object o = clazz.newInstance();
            Field barHeight = clazz.getField("status_bar_height");
            int resId = Integer.parseInt(barHeight.get(o).toString());
            statusBarHeight = getResources().getDimensionPixelSize(resId);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
            params.height = params.height + statusBarHeight;
            toolbar.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w(TAG, "initComponentView: statusBarHeight: " + statusBarHeight);

//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
//
//        layoutParams.setMargins(0, statusBarHeight, 0, 0);
//        toolbar.setLayoutParams(layoutParams);

        toolbar.setPadding(0, statusBarHeight, 0, 0);
    }

    /** 拦截回退键, 判断如果当前Drawer处于打开状态,则关闭Drawer. */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /** Drawer中的NavigationView的item的选项的事件触发 */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camera:
                Toaster.toast(getApplicationContext(), "nav_camera");
                break;
            case R.id.nav_gallery:
                Toaster.toast(getApplicationContext(), "nav_gallery");
                break;
            case R.id.nav_slideshow:
                Toaster.toast(getApplicationContext(), "nav_slideshow");
                break;
            case R.id.nav_manage:
                Toaster.toast(getApplicationContext(), "nav_manage");
                break;
            case R.id.nav_share:
                Toaster.toast(getApplicationContext(), "nav_share");
                break;
            case R.id.nav_send:
                Toaster.toast(getApplicationContext(), "nav_send");
                break;
        }
        return false;
    }

    @Override
    protected Fragment initComponentFragment() {
        return new MainPageFragment();
    }
}

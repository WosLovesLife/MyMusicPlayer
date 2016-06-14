package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.project.myutilslibrary.Toaster;
import com.project.myutilslibrary.wrapper_picture.BlurUtils;
import com.zhangheng.mymusicplayer.MusicApp;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.fragment.MainPageFragment;
import com.zhangheng.mymusicplayer.fragment.OffTimerDialogFragment;
import com.zhangheng.mymusicplayer.listener.OnOffTimerListener;

import java.lang.reflect.Field;

/**
 * Created by zhangH on 2016/4/30.
 */
public class MainPageActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainPageActivity";

    private LinearLayout mPlayerBg;

    OnOffTimerListener mOffTimerListener;

    @Override
    protected int inflateView() {
        return R.layout.drawer_main;
    }

    @Override
    protected void bindToolbarAndDrawer(Toolbar toolbar) {
        super.bindToolbarAndDrawer(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            sinkStatusBar(toolbar);
        }

        /** 设置Drawer和Toolbar的开启关系 */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        /** 通知系统同步关系 */
        toggle.syncState();

        /** 加载Drawer导航组件,注册事件监听 */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (((MusicApp) getApplication()).isOffTimer()) {
            updateOffTimer(navigationView.getMenu().findItem(R.id.nav_off_timer));
        }

        mPlayerBg = (LinearLayout) findViewById(R.id.playerMainPageBg);
    }

    @Override
    protected void initView() {
        super.initView();

        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar_layout);

        if (appBar == null) return;

        /** 设置Toolbar的背景颜色 */
        appBar.setBackground(getResources().getDrawable(R.drawable.shape_toolbar_bg));
        appBar.setTargetElevation(0);
    }

    /** 设置定时停止播放的状态监听器,实时显示倒计时 */
    private void updateOffTimer(final MenuItem item) {
        mOffTimerListener = new OnOffTimerListener() {
            @Override
            public void onOffTimer(long timerDate) {
                if (timerDate > DateUtils.HOUR_IN_MILLIS) {
                    String date = DateFormat.format("hh:mm:ss", timerDate).toString();
                    Log.w(TAG, "onOffTimer: date: " + date);
                    item.setTitle(date + " 后停止");
                } else if (timerDate > DateUtils.MINUTE_IN_MILLIS) {
                    String date = DateFormat.format("mm:ss", timerDate).toString();
                    Log.w(TAG, "onOffTimer: date:" + date);
                    item.setTitle(date + " 后停止");
                } else {
                    item.setTitle(timerDate / DateUtils.SECOND_IN_MILLIS + "s 后停止");
                }
            }
        };
        ((MusicApp) getApplication()).setOnOffTimerListener(mOffTimerListener);
    }

    /** 设置沉浸式状态栏 */
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
    public boolean onNavigationItemSelected(final MenuItem item) {
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
            case R.id.nav_off_timer:
                updateOffTimer(item);

                OffTimerDialogFragment.newInstance(mOffTimerListener).show(getSupportFragmentManager(), "OffTimer");
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOffTimerListener = null;

        Log.w(TAG, "onDestroy: " );
    }

    @Override
    protected Fragment initFragment() {
        return new MainPageFragment();
    }

    public void setPlayerBg(Bitmap background) {
        if (background != null) {
            BitmapDrawable drawable = BlurUtils.makePictureBlur(getApplicationContext(), background, mPlayerBg, 2, 30);
            mPlayerBg.setBackground(drawable);
        } else {
            mPlayerBg.setBackgroundResource(R.drawable.playpage_background);
        }
    }




    @Override
    public void onStop() {
        super.onStop();
        Log.w(TAG, "onStop: " );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.w(TAG, "onSaveInstanceState: " );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w(TAG, "onCreate: " );
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        Log.w(TAG, "onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState): " );
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.w(TAG, "onRestart: " );
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.w(TAG, "onStart: " );
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.w(TAG, "onResume: " );
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.w(TAG, "onPause: " );
    }

}
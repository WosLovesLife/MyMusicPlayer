package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zhangheng.mymusicplayer.MusicApp;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.fragment.MainPageFragment;
import com.zhangheng.mymusicplayer.fragment.OffTimerDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

/**
 * Created by zhangH on 2016/4/30.
 */
public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainPageActivity";
    private static final String EXTRA_KILL_ACTIVITIES = "extra_kill_activities";
    private static final String EXTRA_KILL_ACTIVITIES_VALUE = "com.zhangheng.mymusicplayer.activity.extra_kill_activities_value";

    public static final int REQUEST_CODE_LIST_FRAGMENT = 0;

    /** 通过setBackground()设置模糊背景 */
    private CoordinatorLayout mPlayerBg;
    private MenuItem mOffTimer;

    /** 通过startActivity()该方法返回的Intent来结束该程序的所有Activity */
    public static Intent getIntent2KillAllActivity(Context packageContext) {
        Intent killAllActivityIntent = new Intent(packageContext, MainPageActivity.class);
        killAllActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        killAllActivityIntent.putExtra(EXTRA_KILL_ACTIVITIES, EXTRA_KILL_ACTIVITIES_VALUE);
        return killAllActivityIntent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        killSelf(savedInstanceState);

        EventBus.getDefault().register(this);

        initView();
        bindToolbarAndDrawer();
        initFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(TAG, "onNewIntent: ");

        Bundle extras = intent.getExtras();

        if (extras == null) return;

        killSelf(extras);
    }

    /**
     * 如果该Activity启动时携带结束意图, 则结束掉本Activity,
     * 因为该Intent规定的启动模式是FLAG_ACTIVITY_NEW_TASK,
     * 所以该Activity结束也代表结束了整个任务栈
     */
    private void killSelf(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String string = savedInstanceState.getString(EXTRA_KILL_ACTIVITIES);
            if (TextUtils.equals(string, EXTRA_KILL_ACTIVITIES_VALUE)) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void initView() {
        mPlayerBg = (CoordinatorLayout) findViewById(R.id.playerMainPageBg);
    }

    protected void bindToolbarAndDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            sinkStatusBar(toolbar);
        }

        /** 设置Drawer和Toolbar的开启关系 通知系统同步关系 */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();

        /** 加载Drawer导航组件,注册事件监听 */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mOffTimer = navigationView.getMenu().findItem(R.id.nav_off_timer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void initFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new MainPageFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOffTimerEvent(MusicApp.OffTimerEvent event) {
        long timerDate = event.timerDate;
        String result = timerDate / DateUtils.SECOND_IN_MILLIS + "s 后停止";

        if (timerDate > DateUtils.HOUR_IN_MILLIS) {
            result = DateFormat.format("hh:mm:ss", timerDate).toString();
        } else if (timerDate > DateUtils.MINUTE_IN_MILLIS) {
            result = DateFormat.format("mm:ss", timerDate).toString();
        }

        mOffTimer.setTitle(result);
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
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /** Drawer中的NavigationView的item的选项的事件触发 */
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_off_timer:    // 定时器
                OffTimerDialogFragment.newInstance().show(getSupportFragmentManager(), "OffTimer");
                return true;
        }
        return false;
    }

    ///////////== 关于ActionBar的方法start==/////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_page_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_list:
                Intent i = new Intent(this, MusicListActivity.class);
                this.startActivityForResult(i, REQUEST_CODE_LIST_FRAGMENT);
                return true;// 表示处理该事件
        }
        return super.onOptionsItemSelected(item);
    }
    ///////////== 关于ActionBar的方法end==/////////////
    public View getBgView() {
        return mPlayerBg;
    }
}
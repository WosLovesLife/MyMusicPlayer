package com.zhangheng.mymusicplayer.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.ui.MainPageFragment;
import com.zhangheng.mymusicplayer.utils.Toaster;

/**
 * Created by zhangH on 2016/4/30.
 */
public class MainPageActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected int initView() {
        return R.layout.drawer_main;
    }

    @Override
    protected void initComponentView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("I Have A Dream");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_page_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_list){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

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

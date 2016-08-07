package com.zhangheng.mymusicplayer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.project.myutilslibrary.view.quickindex.QuickBarWithToast;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.dapter.MusicListAdapter;
import com.zhangheng.mymusicplayer.engine.MusicDispatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 管理歌曲列表界面
 * Created by zhangH on 2016/5/17.
 */
public class MusicListFragment extends Fragment implements  QuickBarWithToast.OnIndexChangedListener {
    private static final String TAG = "MusicListFragment";

    private MusicListAdapter mMusicListAdapter;
    private MusicDispatcher mMusicDispatcher;
    private ArrayList<String> mInitialsArray;

    // View
    private View mView;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private Snackbar mSnackbar;
    private Unbinder mBind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list_page, container, false);

        mBind = ButterKnife.bind(this, mView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMusicDispatcher = MusicDispatcher.newInstance(getActivity());
        mMusicDispatcher.notifyMusicsEventPost();

        QuickBarWithToast quickBarWithToast = (QuickBarWithToast) getActivity().findViewById(R.id.quickIndexBar);
        quickBarWithToast.setOnIndexChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBind.unbind();
    }

    /** 用户滑动列表右边的QuickBar,滚动至对应的界面 */
    @Override
    public void onIndexChanged(String index) {
        Log.w("MusicListFragment", "onIndexChanged: index: " + index);
        int i = mInitialsArray.indexOf(index);

        mRecyclerView.scrollToPosition(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataChangedEvent(MusicDispatcher.DataChangedEvent event){
        if (mMusicListAdapter == null) {
            mMusicListAdapter = new MusicListAdapter();
            mMusicListAdapter.setData(event.mMusicBeanArray);
            mRecyclerView.setAdapter(mMusicListAdapter);
        } else {
            mMusicListAdapter.notifyDataSetChanged();

            makeSnackBar();
            mSnackbar.setText("音乐列表更新了~ 当前共有 " + event.mMusicBeanArray.size() + " 首歌曲");
            /* 扫描歌曲后必然后走该方法，所以将SnackBar的销毁工作也在这里进行 */
            cancelSnackBar();
        }

        mInitialsArray = event.mMusicIndexArray;

        mMusicListAdapter.setPlayedPosition(event.mCurrentIndex);

        mRecyclerView.scrollToPosition(event.mCurrentIndex - 3);
    }

    /** 由于某些原因,例如歌曲不存在等导致播放自动跳到下一首.这种时候需要这里同步位置 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemChangedEvent(MusicDispatcher.ItemChangedEvent event) {
        mMusicListAdapter.setPlayedPosition(event.mCurrentIndex);
    }

    /** 当用户点击某个条目时,通知调度者播放指定的歌曲 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemCLickEvent(MusicListAdapter.MusicItemClickedEvent event){
        mMusicDispatcher.playSelectedItem(event.mPosition);
        Log.w(TAG, "onItemCLickEvent: 收到事件, MusicBean = "+event.mMusicBean+"; position = "+event.mPosition );
    }

    /////// ToolBar初始化 //////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list_page_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                makeSnackBar();
                mSnackbar.setText("正在扫描本地目录...").show();

                mMusicDispatcher.scanSdcardMusics(musicBean -> mSnackbar.setText("找到歌曲: " + musicBean.getMusicName() + " - " + musicBean.getSinger()).show());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeSnackBar() {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(mView, "", Snackbar.LENGTH_INDEFINITE);
        }
    }

    private void cancelSnackBar() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (mSnackbar != null) {
                if (mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
                mSnackbar = null;
            }
        }, 3000);
    }
}

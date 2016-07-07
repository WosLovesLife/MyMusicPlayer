package com.zhangheng.mymusicplayer.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.project.myutilslibrary.view.quickindex.QuickBarWithToast;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.dapter.RecyclerViewAdapter;
import com.zhangheng.mymusicplayer.engine.MusicDispatcher;
import com.zhangheng.mymusicplayer.engine.SearchMusics;
import com.zhangheng.mymusicplayer.listener.OnMusicDispatchDataChangedListener;

import java.util.ArrayList;

/**
 * 管理歌曲列表界面
 * Created by zhangH on 2016/5/17.
 */
public class MusicListFragment extends Fragment implements OnMusicDispatchDataChangedListener, QuickBarWithToast.OnIndexChangedListener, RecyclerViewAdapter.OnItemClickListener {

    private RecyclerViewAdapter mMusicListAdapter;
    private MusicDispatcher mMusicDispatcher;
    private ArrayList<String> mInitialsArray;
    private RecyclerView mRecyclerView;
    private View mView;
    private Snackbar mSnackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list_page, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMusicDispatcher = MusicDispatcher.newInstance(getActivity());
        mMusicDispatcher.setOnMusicDispatchDataChangedListener(this);

        QuickBarWithToast quickBarWithToast = (QuickBarWithToast) getActivity().findViewById(R.id.quickIndexBar);
        quickBarWithToast.setOnIndexChangedListener(this);
    }

    @Override
    public void onDispatchDataChanged(ArrayList<MusicBean> musicBeanArray, ArrayList<String> musicIndexArray, int currentIndex) {
        if (mMusicListAdapter == null) {
            mMusicListAdapter = new RecyclerViewAdapter(musicBeanArray);

            mRecyclerView.setAdapter(mMusicListAdapter);
            mMusicListAdapter.setOnItemClickListener(this);
        } else {
            mMusicListAdapter.notifyDataSetChanged();

            makeSnackBar();
            mSnackbar.setText("音乐列表更新了~ 当前共有 " + musicBeanArray.size() + " 首歌曲");
            /* 扫描歌曲后必然后走该方法，所以将SnackBar的销毁工作也在这里进行 */
            cancelSnackBar();
        }

        mInitialsArray = musicIndexArray;

        mMusicListAdapter.setPlayedPosition(currentIndex);

        mRecyclerView.scrollToPosition(currentIndex - 3);
    }

    int a;

    /** 由于某些原因,例如歌曲不存在等导致播放自动跳到下一首.这种时候需要这里同步位置 */
    @Override
    public void onItemChanged(int currentIndex) {
        mMusicListAdapter.setPlayedPosition(currentIndex);

        Log.w("MusicList", "onItemChanged: " + (++a));
    }

    /** 用户滑动列表右边的QuickBar,滚动至对应的界面 */
    @Override
    public void onIndexChanged(String index) {
        Log.w("MusicListFragment", "onIndexChanged: index: " + index);
        int i = mInitialsArray.indexOf(index);

        mRecyclerView.scrollToPosition(i);
    }

    /** 当用户点击某个条目时,通知调度者播放指定的歌曲 */
    @Override
    public void onItemClickListener(View v, MusicBean music, int position) {
        mMusicDispatcher.playSelectedItem(position);
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

                mMusicDispatcher.scanSdcardMusics(new SearchMusics.OnMusicSearchingListener() {
                    @Override
                    public void foundMusic(MusicBean musicBean) {
                        mSnackbar.setText("找到歌曲: " + musicBean.getMusicName() + " - " + musicBean.getSinger()).show();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeSnackBar() {
        if (mSnackbar == null) {
            View snackBarContainer = mView.findViewById(R.id.snack_bar_container);
            mSnackbar = Snackbar.make(snackBarContainer, "", Snackbar.LENGTH_INDEFINITE);
        }
    }

    private void cancelSnackBar() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSnackbar != null) {
                    if (mSnackbar.isShown()) {
                        mSnackbar.dismiss();
                    }
                    mSnackbar = null;
                }
            }
        }, 3000);
    }
}

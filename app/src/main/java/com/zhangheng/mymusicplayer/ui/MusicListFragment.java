package com.zhangheng.mymusicplayer.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.zhangheng.mymusicplayer.listener.OnMusicDispatchDataChangedListener;
import com.zhangheng.mymusicplayer.utils.Toaster;

import java.util.ArrayList;

/**
 * Created by zhangH on 2016/5/17.
 */
public class MusicListFragment extends Fragment implements OnMusicDispatchDataChangedListener, QuickBarWithToast.OnIndexChangedListener, RecyclerViewAdapter.OnItemClickListener {

    private RecyclerViewAdapter mMusicListAdapter;
    private MusicDispatcher mMusicDispatcher;
    private ArrayList<String> mInitialsArray;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_page, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
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
            Toaster.toast(getActivity(), "音乐列表更新了~");
        }
        mInitialsArray = musicIndexArray;
        mMusicListAdapter.setPlayedPosition(currentIndex);
        mLayoutManager.scrollToPosition(currentIndex);
    }

    @Override
    public void onFoundLastPlayedMusic(MusicBean musicBean) {
    }

    /** 用户滑动列表右边的QuickBar,滚动至对应的界面 */
    @Override
    public void onIndexChanged(String index) {
        Log.w("MusicListFragment", "onIndexChanged: index: "+index );
        int i = mInitialsArray.indexOf(index);
        mLayoutManager.scrollToPosition(i);
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
                mMusicDispatcher.scanSdcardMusics();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

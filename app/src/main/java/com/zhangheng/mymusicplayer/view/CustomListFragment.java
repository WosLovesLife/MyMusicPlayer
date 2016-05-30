package com.zhangheng.mymusicplayer.view;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangheng.mymusicplayer.R;

/**
 * Created by zhangH on 2016/5/17.
 */
public class CustomListFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_page, container, false);
    }
}

package com.zhangheng.mymusicplayer.engine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.project.myutilslibrary.PinyinUtils;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.exception.PlayerException;
import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.listener.OnMusicDispatchDataChangedListener;
import com.zhangheng.mymusicplayer.listener.OnMusicListItemSelectedListener;
import com.zhangheng.mymusicplayer.utils.SdcardEnableUtils;
import com.zhangheng.mymusicplayer.utils.SharedPreferenceTool;
import com.zhangheng.mymusicplayer.utils.Toaster;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhangH on 2016/5/17.
 * MusicListFragment通过设置监听,来通过ListView的Item的状态
 * Controller通过和此类交互获取歌曲的来源和上一曲下一曲的顺序等.
 */
public class MusicDispatcher {
    private static final String TAG = Constants.TAG;

    private static MusicDispatcher sMusicDispatcher;
    private Context mContext;

    private List<OnMusicDispatchDataChangedListener> mDataChangedListenerList;
    private OnMusicListItemSelectedListener mOnMusicListItemSelectedListener;

    private static ArrayList<MusicBean> sMusicBeanArray;
    private static ArrayList<String> sMusicIndexArray;

    /** 当前的索引 = loopIndex % 数据集合.size() */
    private int mCurrentIndex;
    private boolean mIsSearching;

    private MusicDispatcher(Context context) {
        mContext = context;
        initData();
    }

    public static MusicDispatcher newInstance(
            Context context) {
        if (sMusicDispatcher == null) {
            sMusicDispatcher = new MusicDispatcher(context.getApplicationContext());
        }
        return sMusicDispatcher;
    }

    public void setOnMusicDispatchDataChangedListener(OnMusicDispatchDataChangedListener onMusicDispatchDataChangedListener) {
        if (mDataChangedListenerList == null) {
            mDataChangedListenerList = new ArrayList<>();
        }
        mDataChangedListenerList.add(onMusicDispatchDataChangedListener);
        onMusicDispatchDataChangedListener.onDispatchDataChanged(sMusicBeanArray, sMusicIndexArray, mCurrentIndex);
    }

    public void setOnMusicListItemSelectedListener(OnMusicListItemSelectedListener musicListItemSelectedListener) {
        mOnMusicListItemSelectedListener = musicListItemSelectedListener;
    }

    private void initData() {
        sMusicBeanArray = new ArrayList<>();
        sMusicIndexArray = new ArrayList<>();
        getMusicListFromDatabase();
    }

    private void refreshMusicArray(ArrayList<MusicBean> array, ArrayList<String> indexArray) {
        sMusicBeanArray.clear();
        sMusicBeanArray.addAll(array);
        sMusicIndexArray.clear();
        sMusicIndexArray.addAll(indexArray);
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        for (OnMusicDispatchDataChangedListener listener : mDataChangedListenerList) {
            listener.onDispatchDataChanged(sMusicBeanArray, sMusicIndexArray, mCurrentIndex);
        }
    }

    private void notifyFoundLastSavedMusic(MusicBean musicBean) {
        for (OnMusicDispatchDataChangedListener listener : mDataChangedListenerList) {
            listener.onFoundLastPlayedMusic(musicBean);
        }
    }

    private void getMusicListFromDatabase() {
        AsyncTask4Database asyncTask4Database = new AsyncTask4Database();
        asyncTask4Database.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /** 异步任务,在子线程中读取数据库之前保存的歌曲列表. */
    private class AsyncTask4Database extends AsyncTask<Void, Void, ArrayList<MusicBean>> {

        private ArrayList<String> mTempIndexArray;
        MusicBean mMusicBean = null;

        /** 遍历数据库中的数据,得到当前的歌曲清单 */
        @Override
        protected ArrayList<MusicBean> doInBackground(Void[] params) {
            ArrayList<MusicBean> musicBeansArray = MusicDatabaseEngine.readAll(mContext);
            mTempIndexArray = buildIndexArray(musicBeansArray);
            Collections.sort(musicBeansArray);
            Collections.sort(mTempIndexArray);

            int lastIndex = SharedPreferenceTool.getInteger(mContext, Constants.KEY_LAST_SAVED_MUSIC, 0);

            if (musicBeansArray != null && musicBeansArray.size() > lastIndex) {
                mMusicBean = musicBeansArray.get(lastIndex);
                mCurrentIndex = lastIndex;
            }

            return musicBeansArray;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeansArray) {
            refreshMusicArray(musicBeansArray, mTempIndexArray);
            notifyFoundLastSavedMusic(mMusicBean);
        }
    }

    /** 根据提供的ListView<MusicBean> 生成对应的索引ArrayList<String> */
    private ArrayList<String> buildIndexArray(ArrayList<MusicBean> arrayList) {
        ArrayList<String> indexArray = new ArrayList<>();

        if (arrayList != null && arrayList.size() > 0) {
            for (MusicBean m : arrayList) {
                indexArray.add(m.getPinyin().charAt(0) + "");
            }
        }

        return indexArray;
    }

    /** 刷新歌曲列表内容 ,重新扫描SD卡并更新到数据库 */
    public void scanSdcardMusics() {
        if (!mIsSearching) {
            mIsSearching = true;

            long lastUpdatedTime = SharedPreferenceTool.getLong(mContext, Constants.KEY_LAST_DATABASE_UPDATED_TIME, 0L);
            long currentTime = System.currentTimeMillis();

            Log.w(TAG, "currentTime: " + currentTime + "; lastUpdatedTime: " + lastUpdatedTime);
            Log.w(TAG, "check4updateDatabase: 准备扫描SD卡");

            if (SdcardEnableUtils.isEnable()) {
                searchMusicFromSdcard();
                SharedPreferenceTool.saveLong(mContext, Constants.KEY_LAST_DATABASE_UPDATED_TIME, lastUpdatedTime);
            } else {
                Toaster.toast(mContext, "检测外部存储状态异常!");
                mIsSearching = false;
            }
        }

        Toaster.toast(mContext, "正在扫描音乐,请稍候...");
    }

    private void searchMusicFromSdcard() {
        AsyncTask4ScanSdcard asyncTask4ScanSdcard = new AsyncTask4ScanSdcard();
        asyncTask4ScanSdcard.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /** 异步任务,在子线程中读取数据库之前保存的歌曲列表. */
    private class AsyncTask4ScanSdcard extends AsyncTask<Void, Void, ArrayList<MusicBean>> {

        int count;
        private ArrayList<String> mTempIndexArray;
        private ArrayList<MusicBean> mTempMusicBeansArray;

        /** 遍历SD卡,获取符合规范的歌曲 */
        @Override
        protected ArrayList<MusicBean> doInBackground(Void[] params) {
            mTempMusicBeansArray = new ArrayList<>();
            File sdDir = Environment.getExternalStorageDirectory();
            Log.w(TAG, "doInBackground: externalStoragePublicDirectory： " + sdDir.canRead());
            if (SdcardEnableUtils.isEnable()) {
                scanSdcard(sdDir);
                Collections.sort(mTempMusicBeansArray);
                MusicDatabaseEngine.updateContent(mContext, mTempMusicBeansArray);
            }
            mTempIndexArray = buildIndexArray(mTempMusicBeansArray);
            Collections.sort(mTempMusicBeansArray);
            Collections.sort(mTempIndexArray);
            return mTempMusicBeansArray;
        }

        private void scanSdcard(File dir) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanSdcard(file);
                        Log.w(TAG, "scanSdcard: scanSdcard 执行了:" + (count++));
                    } else {
                        String filePath = file.getAbsolutePath();
                        if (filePath.endsWith(".mp3")) {
                            // 获取到文件名
                            String unknownName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
                            Log.w(TAG, "scanSdcard: subString: " + unknownName);
                            // 依照虾米音乐和网易云音乐的命名规范赋音乐名和歌手名
                            int wyIndex = unknownName.indexOf("-");
                            int xmIndex = unknownName.indexOf("_");
                            String musicName;
                            String singer = "未知";
                            if (wyIndex != -1) {//网易云音乐的命名方式,左边歌手名,右边歌曲名
                                musicName = unknownName.substring(wyIndex + 1).trim();
                                singer = unknownName.substring(0, wyIndex).trim();
                                Log.w(TAG, "scanSdcard: 网易歌曲: 歌名: " + musicName + "; 歌手: " + singer);
                            } else if (xmIndex != -1) {//虾米音乐的命名方式,左边歌曲名,右边歌手名
                                musicName = unknownName.substring(0, xmIndex).trim();
                                singer = unknownName.substring(xmIndex + 1).trim();
                                Log.w(TAG, "scanSdcard: 虾米歌曲: 歌名: " + musicName + "; 歌手: " + singer);
                            } else {
                                musicName = unknownName;
                                Log.w(TAG, "scanSdcard: 未知歌曲: 歌名: " + musicName);
                            }
                            mTempMusicBeansArray.add(new MusicBean(-1, musicName, singer, filePath, PinyinUtils.toPinyin(musicName)));
                        }
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeansArray) {
            refreshMusicArray(musicBeansArray, mTempIndexArray);
            mIsSearching = false;
        }
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    public void playSelectedItem(int position) throws PlayerException {
        mCurrentIndex = position;
        Log.w(TAG, "playSelectedItem: mCurrentIndex: " + mCurrentIndex);
        informPlay();
    }

    public void getDefault() {
        Log.w(TAG, "playSelectedItem: getDefault: " + mCurrentIndex);
        informPlay();
    }

    public void getPre() {
        Log.w(TAG, "playSelectedItem: getPre: " + mCurrentIndex);
        if (--mCurrentIndex <= 0) {
            mCurrentIndex = sMusicIndexArray.size() - 1;
        }
        informPlay();
    }

    public void getNext() {
        Log.w(TAG, "playSelectedItem: getNext: " + mCurrentIndex);
        if (++mCurrentIndex >= sMusicBeanArray.size()) {
            mCurrentIndex = 0;
        }
        informPlay();
    }

    private void informPlay() {
        if (mOnMusicListItemSelectedListener != null) {
            if (sMusicBeanArray.size() > 0) {
                mOnMusicListItemSelectedListener.OnMusicListItemSelected(sMusicBeanArray.get(mCurrentIndex));
            } else {
                mOnMusicListItemSelectedListener.OnMusicListItemSelected(null);
            }
            saveMusic();
        } else {
            Toaster.toast(mContext, "Error: Controller没有注册监听!请反馈开发者.");
        }
    }

    public void saveMusic() {
        SharedPreferenceTool.saveInteger(mContext, Constants.KEY_LAST_SAVED_MUSIC, mCurrentIndex);
    }
}

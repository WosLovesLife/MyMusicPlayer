package com.zhangheng.mymusicplayer.engine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import com.project.myutilslibrary.PinyinUtils;
import com.project.myutilslibrary.SdcardEnableUtils;
import com.project.myutilslibrary.SharedPreferenceTool;
import com.project.myutilslibrary.Toaster;
import com.project.myutilslibrary.mp3agic.ID3v1;
import com.project.myutilslibrary.mp3agic.ID3v2;
import com.project.myutilslibrary.mp3agic.Mp3File;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.global.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 通过本类查询手机中的歌曲,
 * Created by zhangh on 2016/7/7.
 */
public class SearchMusics {

    public interface OnFinishedListener {
        void onFinished(ArrayList<MusicBean> musicBeanList, ArrayList<String> musicIndexList, int savedIndex);
    }

    public interface OnMusicSearchingListener {
        void foundMusic(MusicBean musicBean);
    }

    private static final String TAG = "SearchMusics";
    private static final String KEY_LAST_DATABASE_UPDATED_TIME = "key_last_database_updated_time";

    private static Context sContext;
    private static OnFinishedListener sOnFinishedListener;
    private static OnMusicSearchingListener sOnMusicSearchingListener;
    private static boolean sIsSearching;

    private SearchMusics() {
    }

    ;

    /** 根据提供的ListView<MusicBean> 生成对应的索引ArrayList<String> */
    public static ArrayList<String> buildIndexArray(ArrayList<MusicBean> arrayList) {
        ArrayList<String> indexArray = new ArrayList<>();

        if (arrayList != null && arrayList.size() > 0) {
            for (MusicBean m : arrayList) {
                indexArray.add(m.getPinyin().charAt(0) + "");
            }
        }

        return indexArray;
    }

    /**
     * 从SD卡中获取音乐列表, 并更新到数据库
     *
     * @param context            用于SD卡的相关操作
     * @param onFinishedListener 当音乐扫描完毕 通过该接口的回调方法将结果传出
     */
    public static void getMusicListFromSdCard(Context context, final OnFinishedListener onFinishedListener, OnMusicSearchingListener onMusicSearchingListener) {
        if (sIsSearching) {
            Toaster.toastLong(sContext, "正在扫描音乐,请稍候...");
            return;
        }

        sContext = context;
        sOnFinishedListener = onFinishedListener;
        sOnMusicSearchingListener = onMusicSearchingListener;

        sIsSearching = true;

        long lastUpdatedTime = SharedPreferenceTool.getLong(sContext, KEY_LAST_DATABASE_UPDATED_TIME, 0L);

//        Log.w(TAG, "currentTime: " + currentTime + "; lastUpdatedTime: " + lastUpdatedTime);
//        Log.w(TAG, "check4updateDatabase: 准备扫描SD卡");

        if (SdcardEnableUtils.isEnable()) {

            AsyncTask4ScanSdcard asyncTask4ScanSdcard = new AsyncTask4ScanSdcard();
            asyncTask4ScanSdcard.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            SharedPreferenceTool.saveLong(sContext, KEY_LAST_DATABASE_UPDATED_TIME, lastUpdatedTime);
        } else {
            Toaster.toast(sContext, "检测外部存储状态异常!");
            sIsSearching = false;
        }

    }

    /** 异步任务,在子线程中读取数据库之前保存的歌曲列表. */
    private static class AsyncTask4ScanSdcard extends AsyncTask<Void, Void, ArrayList<MusicBean>> {

        int count;
        private ArrayList<String> mTempIndexArray;
        private ArrayList<MusicBean> mTempMusicBeansArray;

        /** 遍历SD卡,获取符合规范的歌曲 */
        @Override
        protected ArrayList<MusicBean> doInBackground(Void[] params) {
            mTempMusicBeansArray = new ArrayList<>();
            File sdDir = Environment.getExternalStorageDirectory();
//            Log.w(TAG, "doInBackground: externalStoragePublicDirectory： " + sdDir.canRead());
            if (SdcardEnableUtils.isEnable()) {
                scanSdcard(sdDir);
                Collections.sort(mTempMusicBeansArray);
                MusicDatabaseEngine.updateContent(sContext, mTempMusicBeansArray);
            }
            mTempIndexArray = buildIndexArray(mTempMusicBeansArray);
            Collections.sort(mTempMusicBeansArray);
            Collections.sort(mTempIndexArray);
            return mTempMusicBeansArray;
        }

        private void scanSdcard(File dir) {
            if (dir == null) return;

            File[] files = dir.listFiles();

            if (files == null || files.length <= 0) return;

            for (File file : files) {
                if (file.isDirectory()) {
                    scanSdcard(file);
//                    Log.w(TAG, "scanSdcard: scanSdcard 执行了:" + (count++));
                } else {
                    filterFile(file);
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeansArray) {
            if (sOnFinishedListener != null) {
                sOnFinishedListener.onFinished(musicBeansArray, mTempIndexArray, 0);
                sOnFinishedListener = null;
            }
            sIsSearching = false;
        }

        private void filterFile(File file) {
            String filePath = file.getAbsolutePath();
            if (filePath.endsWith(".mp3")) {
                // 获取到文件名
                String unknownName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
                String musicName = unknownName;
                String singer = "未知";
                try {
                    Mp3File m = new Mp3File(filePath);
                    int lengthInSeconds = (int) m.getLengthInSeconds();
                    if (lengthInSeconds > 90) {
                        if (m.hasId3v2Tag()) {
                            ID3v2 id3v2Tag = m.getId3v2Tag();
                            musicName = id3v2Tag.getTitle();
                            singer = id3v2Tag.getArtist();
                        } else if (m.hasId3v1Tag()) {
                            ID3v1 id3v1Tag = m.getId3v1Tag();
                            musicName = id3v1Tag.getTitle();
                            singer = id3v1Tag.getArtist();
                        }
                        final MusicBean musicBean = new MusicBean(-1, musicName, singer, filePath, PinyinUtils.toPinyin(musicName), lengthInSeconds);

                        new Handler(sContext.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (sOnMusicSearchingListener != null) {
                                    sOnMusicSearchingListener.foundMusic(musicBean);
                                }
                            }
                        });
                        mTempMusicBeansArray.add(musicBean);
                    }
//                    Log.w(TAG, "scanSdcard: getLengthInSeconds: " + lengthInSeconds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        /**
         * 从数据库中获取已经存在的歌曲列表
         * @param context 操作数据库的上下文
         * @param onFinishedListener 当数据读取完毕时调用
         */
    }

    public static void getMusicListFromDatabase(Context context, final OnFinishedListener onFinishedListener) {
        sContext = context;
        sOnFinishedListener = onFinishedListener;

        AsyncTask4Database asyncTask4Database = new AsyncTask4Database();
        asyncTask4Database.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /** 异步任务,在子线程中读取数据库之前保存的歌曲列表. */
    private static class AsyncTask4Database extends AsyncTask<Void, Void, ArrayList<MusicBean>> {

        private ArrayList<String> mTempIndexArray;
        private int mCurrentIndex;

        /** 遍历数据库中的数据,得到当前的歌曲清单 */
        @Override
        protected ArrayList<MusicBean> doInBackground(Void[] params) {
            ArrayList<MusicBean> musicBeansArray = MusicDatabaseEngine.readAll(sContext);
            mTempIndexArray = SearchMusics.buildIndexArray(musicBeansArray);
            Collections.sort(musicBeansArray);
            Collections.sort(mTempIndexArray);

            mCurrentIndex = SharedPreferenceTool.getInteger(sContext, Constants.KEY_LAST_SAVED_MUSIC, 0);

            return musicBeansArray;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeansArray) {
            if (sOnFinishedListener != null) {
                sOnFinishedListener.onFinished(musicBeansArray, mTempIndexArray, mCurrentIndex);
                sOnFinishedListener = null;
            }
        }
    }
}

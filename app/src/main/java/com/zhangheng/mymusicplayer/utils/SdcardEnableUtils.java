package com.zhangheng.mymusicplayer.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by zhangH on 2016/5/17.
 */
public class SdcardEnableUtils {

    public static boolean isEnable() {
        String storageState = Environment.getExternalStorageState();
        return storageState.equals(Environment.MEDIA_MOUNTED);
    }

    public static long getSdSpace() {
        File directory = Environment.getExternalStorageDirectory();
        return directory.getFreeSpace();
    }
}

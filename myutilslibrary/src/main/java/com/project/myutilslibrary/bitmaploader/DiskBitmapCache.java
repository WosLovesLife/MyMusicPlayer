package com.project.myutilslibrary.bitmaploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.project.myutilslibrary.CloseStreamTool;
import com.project.myutilslibrary.MD5Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by zhangH on 2016/5/15.
 */
public class DiskBitmapCache {

    private Context mContext;
    private File mDirs;

    public DiskBitmapCache(Context context) {
        mContext = context;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            mDirs = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "zbhj_cache");
            if (!mDirs.exists()) {
                mDirs.mkdirs();
            }
            File[] files = mDirs.listFiles();
            for (File f :
                    files) {
                f.delete();
            }
        }
    }

    private boolean isExternalAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        if (TextUtils.equals(externalStorageState, Environment.MEDIA_MOUNTED)) {
            return true;
        }
        Log.w("DiskBitmapCache", "isExternalAvailable: ExternalIsNotAvailable");
        return false;
    }

    public Bitmap getCache(String url) {
        if (!isExternalAvailable()) {
            return null;
        }
        String md5 = MD5Utils.getMd5(url);
        File file = new File(mDirs, md5);
        if (!file.exists()) {
            return null;
        }
        Bitmap bitmap = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            CloseStreamTool.close(bufferedInputStream);
        }
        return bitmap;
    }

    public void saveCache(Bitmap bitmap, String url) {
        if (!isExternalAvailable()) {
            return;
        }
        String md5 = MD5Utils.getMd5(url);
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(mDirs, md5)));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            CloseStreamTool.close(bufferedOutputStream);
        }
    }
}

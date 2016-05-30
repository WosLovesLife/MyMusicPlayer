package com.project.myutilslibrary.bitmaploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by zhangH on 2016/5/15.
 */
public class MemoryBitmapCache {
    private LruCache<String,Bitmap> mLruCache;

    public MemoryBitmapCache(Context context) {
        int cacheSize = (int) Runtime.getRuntime().maxMemory()/8;
        Log.w("MemoryBitmapCache", "MemoryBitmapCache: cacheSize: "+cacheSize );
        mLruCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public Bitmap getCache(String url) {
        return mLruCache.get(url);
    }

    public void saveCache(Bitmap bitmap, String url) {
        mLruCache.put(url,bitmap);
    }
}

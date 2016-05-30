package com.project.myutilslibrary.pictureloader;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by zhangH on 2016/5/27.
 */
public class MemoryCache {

    private LruCache<String,Bitmap> mLruCache;

    public MemoryCache(){
        mLruCache = new LruCache<String,Bitmap>((int) (Runtime.getRuntime().maxMemory()/8)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public Bitmap getCache(String picPath){
        return mLruCache.get(picPath);
    }

    public void saveCache(String picPath,Bitmap bitmap){
        mLruCache.put(picPath,bitmap);
    }

    public void deleteCache(String picPath) {
        mLruCache.remove(picPath);
    }
}

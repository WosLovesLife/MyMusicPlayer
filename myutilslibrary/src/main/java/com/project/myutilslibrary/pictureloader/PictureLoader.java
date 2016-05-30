package com.project.myutilslibrary.pictureloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.project.myutilslibrary.mp3info.ReadID3v2;

/**
 * Created by zhangH on 2016/5/27.
 */
public class PictureLoader {
    public static final String TAG = "PictureLoader";

    private static PictureLoader sPictureLoader;
    private final MemoryCache mMemoryCache;
    private Activity mActivity;
    private final MemoryCache mMemoryThumbnailCache;

    private PictureLoader(Activity activity) {
        mActivity = activity;
        mMemoryCache = new MemoryCache();
        mMemoryThumbnailCache = new MemoryCache();
    }

    public static PictureLoader newInstance(Activity activity) {
        if (sPictureLoader == null) {
            synchronized (PictureLoader.class) {
                if (sPictureLoader == null) {
                    sPictureLoader = new PictureLoader(activity);
                }
            }
        }
        return sPictureLoader;
    }

    public void setImageViewPictureWithCache(String picPath, ImageView imageView) {
        getCache(picPath, imageView, mMemoryCache);
    }

    public void setImageViewThumbnailPictureWithCache(String picPath, ImageView imageView) {
        getCache(picPath, imageView, mMemoryThumbnailCache);
    }

    private void getCache(String picPath, ImageView imageView, MemoryCache memoryCache) {
        if (picPath == null || imageView == null) {
            return;
        }

        Bitmap cache = memoryCache.getCache(picPath);
        if (cache != null) {
            imageView.setImageBitmap(cache);
            return;
        }

//        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        imageView.measure(width,height);
//        width = imageView.getMeasuredWidth();
//        height = imageView.getMeasuredHeight();
//        cache = PictureScaleUtils.getScaledBitmap(picPath, width, height);

        cache = PictureScaleUtils.getScaledBitmap(picPath, mActivity);

        imageView.setImageBitmap(cache);
        if (cache != null) {
            memoryCache.saveCache(picPath, cache);
        }
    }

    public void deleteSpecificCache(String picPath) {
        mMemoryCache.deleteCache(picPath);
    }

    public void deleteSpecificThumbnailCache(String picPath) {
        mMemoryThumbnailCache.deleteCache(picPath);
    }


    /** 从Mp3IDv2中获取专辑图片 */
    public void setPictureFromBytesWithCache(String picPath, ImageView imageView) {
        getCache(picPath, imageView);
    }

    private void getCache(final String picPath, final ImageView imageView) {
        if (picPath == null || imageView == null) {
            return;
        }

        try {
            final byte[] apic = ReadID3v2.getMp3Info(picPath).getApic();

            final Bitmap[] cache = {mMemoryCache.getCache(picPath)};
            if (cache[0] != null) {
                imageView.setImageBitmap(cache[0]);
                return;
            }

            new Thread(){
                @Override
                public void run() {
                    cache[0] = PictureScaleUtils.getScaledBitmap(apic, mActivity);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(cache[0]);
                            if (cache[0] != null) {
                                mMemoryCache.saveCache(picPath, cache[0]);
                            }
                            Log.w(TAG, "run: mMemoryCache.saveCache(picPath, cache[0]);"+cache[0] );
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

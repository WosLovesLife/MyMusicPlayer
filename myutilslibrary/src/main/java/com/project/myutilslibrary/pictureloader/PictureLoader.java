package com.project.myutilslibrary.pictureloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

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
        getCache(picPath,imageView,mMemoryCache);
    }

    public void setImageViewThumbnailPictureWithCache(String picPath, ImageView imageView){
        getCache(picPath,imageView,mMemoryThumbnailCache);
    }

    private void getCache(String picPath, ImageView imageView, MemoryCache memoryCache){
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

        cache = PictureScaleUtils.getScaledBitmap(picPath,mActivity);

        imageView.setImageBitmap(cache);
        if (cache != null) {
            memoryCache.saveCache(picPath, cache);
        }
    }

    public void deleteSpecificCache(String picPath){
        mMemoryCache.deleteCache(picPath);
    }

    public void deleteSpecificThumbnailCache(String picPath){
        mMemoryThumbnailCache.deleteCache(picPath);
    }
}

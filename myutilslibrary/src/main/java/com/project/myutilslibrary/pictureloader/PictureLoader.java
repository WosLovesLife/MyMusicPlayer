package com.project.myutilslibrary.pictureloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.project.myutilslibrary.mp3agic.ID3v2;
import com.project.myutilslibrary.mp3agic.Mp3File;

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

    public void setImageViewPictureWithCache(final String picPath, final ImageView imageView) {
        if (imageView.getMeasuredHeight() == 0) {
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    getCache(picPath, imageView, mMemoryCache);
                    return true;
                }
            });
        } else {
            getCache(picPath, imageView, mMemoryCache);
        }
    }

    public void setImageViewThumbnailPictureWithCache(final String picPath, final ImageView imageView) {
        if (imageView.getMeasuredHeight() == 0) {
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    getCache(picPath, imageView, mMemoryThumbnailCache);
                    return true;
                }
            });
        } else {
            getCache(picPath, imageView, mMemoryThumbnailCache);
        }
    }

    private void getCache(String picPath, final ImageView imageView, MemoryCache memoryCache) {
        if (picPath == null || imageView == null) {
            return;
        }

        Bitmap cache = memoryCache.getCache(picPath);
        if (cache != null) {
            imageView.setImageBitmap(cache);
            return;
        }

        cache = PictureScaleUtils.getScaledBitmap(picPath, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());

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


    public void setCacheBitmapFromMp3Idv3(final String picPath, final ImageView imageView) {
        if (imageView.getMeasuredHeight() == 0) {
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    getCacheBitmap(picPath, imageView);
                    return true;
                }
            });
        } else {
            getCacheBitmap(picPath, imageView);
        }
    }

    private void getCacheBitmap(String picPath, ImageView imageView) {
        if (picPath == null) {
            return;
        }

        Bitmap cache = mMemoryCache.getCache(picPath);
        if (cache != null) {
            imageView.setImageBitmap(cache);
            return;
        }

//        try {
//            Mp3File mp3File = new Mp3File(picPath);
//            if (mp3File.hasId3v2Tag()) {
//                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
//                byte[] albumImage = id3v2Tag.getAlbumImage();
//                if (albumImage != null) {
////                    cache = PictureScaleUtils.getScaledBitmap(albumImage, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
//                    cache = PictureScaleUtils.getScaledBitmap(albumImage, mActivity);
//                    imageView.setImageBitmap(cache);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (cache != null) {
//            mMemoryCache.saveCache(picPath, cache);
//        }

        PictureAsyncTask pictureAsyncTask = new PictureAsyncTask(picPath,imageView);
        pictureAsyncTask.executeOnExecutor(PictureAsyncTask.THREAD_POOL_EXECUTOR);
    }

    class PictureAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
        String mPicPath;
        ImageView mImageView;
        private int mMeasuredWidth;
        private int mMeasuredHeight;

        PictureAsyncTask(String picPath, ImageView imageView) {
            mPicPath = picPath;
            mImageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mMeasuredWidth = mImageView.getMeasuredWidth();
            mMeasuredHeight = mImageView.getMeasuredHeight();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap cache = null;
            try {
                Mp3File mp3File = new Mp3File(mPicPath);
                if (mp3File.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                    byte[] albumImage = id3v2Tag.getAlbumImage();
                    if (albumImage != null) {
//                        cache = PictureScaleUtils.getScaledBitmap(albumImage, mMeasuredWidth, mMeasuredHeight);
                        cache = PictureScaleUtils.getScaledBitmap(albumImage, mActivity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cache;
        }

        @Override
        protected void onPostExecute(Bitmap cache) {
            super.onPostExecute(cache);

            mImageView.setImageBitmap(cache);

            if (cache != null) {
                mMemoryCache.saveCache(mPicPath, cache);
            }
        }
    }
}

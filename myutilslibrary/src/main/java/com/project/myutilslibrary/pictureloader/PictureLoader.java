package com.project.myutilslibrary.pictureloader;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.project.myutilslibrary.mp3agic.ID3v2;
import com.project.myutilslibrary.mp3agic.InvalidDataException;
import com.project.myutilslibrary.mp3agic.Mp3File;
import com.project.myutilslibrary.mp3agic.UnsupportedTagException;
import com.project.myutilslibrary.view_utils.ViewSize;

import java.io.IOException;

/**
 * Created by zhangH on 2016/5/27.
 */
public class PictureLoader {
    public static final String TAG = "PictureLoader";

    private static PictureLoader sPictureLoader;
    private final MemoryCache mMemoryCache;
    //    private Activity mActivity;
    private final MemoryCache mMemoryThumbnailCache;

//    private PictureLoader(Activity activity) {

    public interface OnPictureLoadHandleListener {
        void onPictureLoadComplete(Bitmap bitmap);
    }

    private PictureLoader() {
//        mActivity = activity;
        mMemoryCache = new MemoryCache();
        mMemoryThumbnailCache = new MemoryCache();
    }

    public static PictureLoader newInstance() {
        if (sPictureLoader == null) {
            synchronized (PictureLoader.class) {
                if (sPictureLoader == null) {
//                    sPictureLoader = new PictureLoader(activity);
                    sPictureLoader = new PictureLoader();
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

    public void setCacheBitmapFromMp3Idv3(final OnPictureLoadHandleListener listener, final String picPath, final View imageView, int imageViewWidth, int imageViewHeight) {
        if (picPath == null) {
            return;
        }

        Bitmap cache = mMemoryCache.getCache(picPath);
        if (cache != null) {
            listener.onPictureLoadComplete(cache);
            return;
        }

//        if (imageViewWidth <= 0 || imageViewHeight <= 0) {
//            if (imageView.getMeasuredHeight() == 0) {
//                imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        imageView.getViewTreeObserver().removeOnPreDrawListener(this);
//
//                        getCacheBitmap(listener, picPath, imageView, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
//                        return true;
//                    }
//                });
//            } else {
//                getCacheBitmap(listener, picPath, imageView, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
//            }
//        } else {
//            getCacheBitmap(listener, picPath, imageView, imageViewWidth, imageViewHeight);
//        }

        ViewSize.getSize(imageView, new ViewSize.OnGotSizeListener() {
            @Override
            public void onGetSize(Point size) {
                getCacheBitmap(listener, picPath, imageView, size.x, size.y);
            }
        });
    }

    private void getCacheBitmap(OnPictureLoadHandleListener listener, String picPath, View imageView, int imageViewWidth, int imageViewHeight) {
        PictureAsyncTask pictureAsyncTask = new PictureAsyncTask(listener, picPath, imageView, imageViewWidth, imageViewHeight);
        pictureAsyncTask.executeOnExecutor(PictureAsyncTask.THREAD_POOL_EXECUTOR);
    }

    class PictureAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
        String mPicPath;
        View mImageView;
        private int mMeasuredWidth;
        private int mMeasuredHeight;
        OnPictureLoadHandleListener mListener;

        PictureAsyncTask(OnPictureLoadHandleListener listener, String picPath, View imageView, int width, int height) {
            mListener = listener;
            mPicPath = picPath;
            mImageView = imageView;
            mMeasuredWidth = width;
            mMeasuredHeight = height;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            try {
                return getAlbumFromMp3(mPicPath, mMeasuredWidth, mMeasuredHeight);
            } catch (InvalidDataException | IOException | UnsupportedTagException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap cache) {
            super.onPostExecute(cache);

            mListener.onPictureLoadComplete(cache);

            if (cache != null) {
                mMemoryCache.saveCache(mPicPath, cache);
            }
        }
    }

    @Nullable
    public static Bitmap getAlbumFromMp3(String picPath, int width, int height) throws InvalidDataException, IOException, UnsupportedTagException {
        Bitmap cache = null;
        Mp3File mp3File = new Mp3File(picPath);
        if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            byte[] albumImage = id3v2Tag.getAlbumImage();
            if (albumImage != null) {
                cache = PictureScaleUtils.getScaledBitmap(albumImage, width, height);
            }
        }
        return cache;
    }
}

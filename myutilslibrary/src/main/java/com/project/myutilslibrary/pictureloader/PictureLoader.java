package com.project.myutilslibrary.pictureloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.project.myutilslibrary.mp3agic.ID3v2;
import com.project.myutilslibrary.mp3agic.InvalidDataException;
import com.project.myutilslibrary.mp3agic.Mp3File;
import com.project.myutilslibrary.mp3agic.UnsupportedTagException;

import java.io.IOException;

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


    public void setCacheBitmapFromMp3Idv3(String picPath, ImageView imageView) {
        if (picPath == null || imageView == null) {
            return;
        }

        Bitmap cache = mMemoryCache.getCache(picPath);
        if (cache != null) {
            imageView.setImageBitmap(cache);
            return;
        }

        try {
            Mp3File mp3File = new Mp3File(picPath);
            if (mp3File.hasId3v2Tag()){
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                byte[] albumImage = id3v2Tag.getAlbumImage();
                if (albumImage!=null){
                    cache = BitmapFactory.decodeByteArray(albumImage,0,albumImage.length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(cache);
        if (cache != null) {
            mMemoryCache.saveCache(picPath, cache);
        }
    }
}

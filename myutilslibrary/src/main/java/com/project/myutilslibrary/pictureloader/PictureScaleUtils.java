package com.project.myutilslibrary.pictureloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by zhangH on 2016/5/27.
 */
public class PictureScaleUtils {
    public static Bitmap getScaledBitmap(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        float realWidth = options.outWidth;
        float realHeight = options.outHeight;

        int scale = 1;
        if (realWidth > width || realHeight > height) {
            if (realWidth > realHeight){
                scale = Math.round(realWidth/width);
            }else {
                scale = Math.round(realHeight/height);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        return BitmapFactory.decodeFile(path,options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path,size.x,size.y);
    }



    public static Bitmap getScaledBitmap(byte[] apic, int width, int height){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(apic,0,apic.length,options);

        float realWidth = options.outWidth;
        float realHeight = options.outHeight;

        int scale = 1;
        if (realWidth > width || realHeight > height) {
            if (realWidth > realHeight){
                scale = Math.round(realWidth/width);
            }else {
                scale = Math.round(realHeight/height);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        return BitmapFactory.decodeByteArray(apic, 0, apic.length, options);
    }

    public static Bitmap getScaledBitmap(byte[] apic, Activity activity){

        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        int height = size.y;
        int width = size.x;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(apic,0,apic.length,options);

        float realWidth = options.outWidth;
        float realHeight = options.outHeight;

        int scale = 1;
        if (realWidth > width || realHeight > height) {
            if (realWidth > realHeight){
                scale = Math.round(realWidth/width);
            }else {
                scale = Math.round(realHeight/height);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        return BitmapFactory.decodeByteArray(apic, 0, apic.length, options);
    }
}

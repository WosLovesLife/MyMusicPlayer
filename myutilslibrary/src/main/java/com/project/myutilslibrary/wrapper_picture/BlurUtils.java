package com.project.myutilslibrary.wrapper_picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

/**
 * Created by zhangH on 2016/6/1.
 */
public class BlurUtils {

    private static final String TAG = "BlurUtils";

    public static BitmapDrawable makePictureBlur(Context context, Bitmap bkg, View view , float scaleFactor, float radius) {
        long startMs = System.currentTimeMillis();

        if (scaleFactor <=0){
            scaleFactor = 1;
        }
        if (radius <= 0){
            radius = 20;
        }
        Log.w(TAG, "makePictureBlur: "+ view.getMeasuredHeight() );

//        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
//                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Bitmap overlay = Bitmap.createBitmap((int) (bkg.getWidth() / scaleFactor),
                (int) (bkg.getHeight() / scaleFactor), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
//        view.setBackground(new BitmapDrawable(context.getResources(), overlay));

        Log.w(TAG, "blur: currentTimeMillis: " + (System.currentTimeMillis() - startMs));
        return new BitmapDrawable(context.getResources(), overlay);
    }
}

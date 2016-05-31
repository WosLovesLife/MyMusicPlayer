package com.project.myutilslibrary.wrapper_picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by zhangH on 2016/5/31.
 */
public class BlurPicture {

    private static final String TAG = "BlurPicture";

    public static void blur (final Context context, final Bitmap bitmap, final View view){
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);

                apply(context,bitmap, view);
                return true;
            }
        });
    }

    private static void apply(Context context, Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();

        float scaleFactor = 8;
        float radius = 2;

        Log.w(TAG, "blur: height: "+view.getMeasuredHeight()+"; width: "+view.getMeasuredWidth());

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int)radius, true);

        view.setBackground(new BitmapDrawable(context.getResources(), overlay));

        Log.w(TAG, "blur: currentTimeMillis: "+ (System.currentTimeMillis() - startMs));
    }
}

package com.project.myutilslibrary.view_utils;

import android.graphics.Point;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by YesingBeijing on 2016/8/10.
 */
public class ViewSize {

    public interface OnGotSizeListener {
        void onGetSize(Point size);
    }

    public static void getSize(final View view, final OnGotSizeListener listener) {
        final Point size = new Point(0, 0);
        if (null != view) {
            if (view.getMeasuredWidth() == 0 && view.getMeasuredHeight() == 0){
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        listener.onGetSize(size);
                        return true;
                    }
                });
            }else {
                size.x = view.getMeasuredWidth();
                size.y = view.getMeasuredHeight();
            }
        } else {
            listener.onGetSize(size);
        }
    }
}

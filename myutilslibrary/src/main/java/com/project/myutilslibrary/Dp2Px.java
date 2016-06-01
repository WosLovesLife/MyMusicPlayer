package com.project.myutilslibrary;

import android.content.Context;

/**
 * Created by zhangH on 2016/5/21.
 */
public class Dp2Px {
    public static int toPX(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp);
    }
}

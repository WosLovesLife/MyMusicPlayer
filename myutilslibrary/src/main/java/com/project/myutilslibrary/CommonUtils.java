package com.project.myutilslibrary;

import android.content.Context;

public class CommonUtils {
    //dip转px
    public static int dip2px(Context context, int dip) {
        //px = dip * 逻辑密度
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    //px转dip
    public static int px2dip(Context context, int px) {
        //px = dip * 逻辑密度
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }
//	//运行任务在主线程
//	public static void runInMainThread(Runnable task){
//
//		if(GooglePlayApp.isMainThread(android.os.Process.myTid())){
//			//如果当前线程是主线程，直接调用task.run()
//			task.run();
//		}else{
//			//如果当前线程是子线程，把task交给主线程去执行
//			GooglePlayApp.getMainHandler().post(task);
//		}
//
//	}
}

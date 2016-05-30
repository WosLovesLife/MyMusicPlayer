package com.zhangheng.mymusicplayer.utils;

import android.util.Log;

public class Logger {
	private static final String TAG = "MobileSafety";
	private static final boolean ON_PROGRAMMING = true;
	
	public static void v(String s){
		if(ON_PROGRAMMING){
			Log.v(TAG, s);
		}
	}
	
	public static void i(String s){
		if(ON_PROGRAMMING){
			Log.i(TAG, s);
		}
	}
	
	public static void d(String s){
		if(ON_PROGRAMMING){
			Log.d(TAG, s);
		}
	}
	
	public static void w(String s){
		if(ON_PROGRAMMING){
			Log.w(TAG, s);
		}
	}
	
	public static void e(String s){
		if(ON_PROGRAMMING){
			Log.e(TAG, s);
		}
	}
}

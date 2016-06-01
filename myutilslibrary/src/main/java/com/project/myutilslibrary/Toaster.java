package com.project.myutilslibrary;

import android.content.Context;
import android.widget.Toast;

import com.project.myutilslibrary.base.BaseApplication;

public class Toaster {
	private static Toast toast;
	
	public static void toast(Context con, String s){
		if(toast != null){
			toast.cancel();
		}
		toast = Toast.makeText(BaseApplication.getGlobalContext(), s, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void toastLong(Context con, String s){
		if(toast != null){
			toast.cancel();
		}
//		toast = Toast.makeText(con, s, Toast.LENGTH_LONG);
		toast = Toast.makeText(BaseApplication.getGlobalContext(), s, Toast.LENGTH_LONG);
		toast.show();
	}
}

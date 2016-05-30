package com.project.myutilslibrary;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

public class ServiceStateUtils {

	public static boolean isRunning(Context con, Class<? extends Service> clazz){
		boolean flag = false;
		
		ActivityManager am = (ActivityManager) con.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(1000);
		for (RunningServiceInfo rsi : services) {
			ComponentName componentName = rsi.service;
			if(TextUtils.equals(componentName.getClassName(), clazz.getName())){
				flag = true;
			}
		}
		
		return flag;
	}
}

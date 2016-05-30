package com.zhangheng.mymusicplayer.utils;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceTool {
	
	private static final String SETTING_OPTION_FILE_NAME = "setting_option";
	
	private static SharedPreferences getPreference(Context con){
		return con.getSharedPreferences(SETTING_OPTION_FILE_NAME, Context.MODE_PRIVATE);
	}
	
	private static  Editor getEditor(Context con){
		SharedPreferences preferences = getPreference(con);
		return preferences.edit();
	}

	public static void saveString(Context con, String key, String value) {
		getEditor(con).putString(key, value).commit();
	}

	public static void saveInteger(Context con, String key, int value) {
		getEditor(con).putInt(key, value).commit();
	}

	public static void saveLong(Context con, String key, long value) {
		getEditor(con).putLong(key, value).commit();
	}

	public static void saveFloat(Context con, String key, float value) {
		getEditor(con).putFloat(key, value).commit();
	}
	
	public static void saveBoolean(Context con, String key, boolean values) {
		getEditor(con).putBoolean(key, values).commit();
	}

	public static void saveStringSet(Context con, String key, Set<String> values) {
		getEditor(con).putStringSet(key, values).commit();
	}
	
	public static String getString(Context con, String key, String defValue){
		return getPreference(con).getString(key, defValue);
	}
	
	public static int getInteger(Context con, String key, int defValue){
		return getPreference(con).getInt(key, defValue);
	}
	
	public static float getFloat(Context con, String key, float defValue){
		return getPreference(con).getFloat(key, defValue);
	}
	
	public static long getLong(Context con, String key, long defValue){
		return getPreference(con).getLong(key, defValue);
	}
	
	public static boolean getBoolean(Context con, String key, boolean defValue){
		return getPreference(con).getBoolean(key, defValue);
	}
	
	public static Set<String> getStringSet(Context con, String key, Set<String> defValues){
		return getPreference(con).getStringSet(key, defValues);
	}
}

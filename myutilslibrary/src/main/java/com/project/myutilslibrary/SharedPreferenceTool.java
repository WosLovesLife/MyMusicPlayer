package com.project.myutilslibrary;

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

	public static void saveData(Context con, String key, String value) {
		getEditor(con).putString(key, value).commit();
	}

	public static void saveData(Context con, String key, int value) {
		getEditor(con).putInt(key, value).commit();
	}

	public static void saveData(Context con, String key, long value) {
		getEditor(con).putLong(key, value).commit();
	}

	public static void saveData(Context con, String key, float value) {
		getEditor(con).putFloat(key, value).commit();
	}
	
	public static void saveData(Context con, String key, boolean values) {
		getEditor(con).putBoolean(key, values).commit();
	}

	public static void saveData(Context con, String key, Set<String> values) {
		getEditor(con).putStringSet(key, values).commit();
	}
	
	public static String getData(Context con, String key, String defValue){
		return getPreference(con).getString(key, defValue);
	}
	
	public static int getData(Context con, String key, int defValue){
		return getPreference(con).getInt(key, defValue);
	}
	
	public static float getData(Context con, String key, float defValue){
		return getPreference(con).getFloat(key, defValue);
	}
	
	public static long getData(Context con, String key, long defValue){
		return getPreference(con).getLong(key, defValue);
	}
	
	public static boolean getData(Context con, String key, boolean defValue){
		return getPreference(con).getBoolean(key, defValue);
	}
	
	public static Set<String> getData(Context con, String key, Set<String> defValues){
		return getPreference(con).getStringSet(key, defValues);
	}
}

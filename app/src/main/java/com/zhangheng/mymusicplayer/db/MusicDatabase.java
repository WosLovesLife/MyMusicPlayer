package com.zhangheng.mymusicplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.global.DatabaseConstants;

/**
 * Created by zhangH on 2016/5/17.
 */
public class MusicDatabase extends SQLiteOpenHelper {


    private static final String TAG = Constants.TAG;

    public MusicDatabase(Context context) {
        super(context, DatabaseConstants.MUSIC_DATABASE_NAME,
                DatabaseConstants.MUSIC_DATABASE_CURSOR_FACTORY,
                DatabaseConstants.MUSIC_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.SQL_MUSIC_TABLE_CREATE);
        Log.w(TAG, "MusicDatabase onCreate: " );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseConstants.DROP_TABLE);
        Log.w(TAG, "MusicDatabase onUpgrade: " );
    }
}

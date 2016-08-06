package com.zhangheng.mymusicplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zhangheng.mymusicplayer.db.MusicDbSchema.MusicTable;
import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.global.DatabaseConstants;

/**
 * Created by zhangH on 2016/5/17.
 */
public class MusicDatabase extends SQLiteOpenHelper {

    /** 数据库名 */
    private static final String MUSIC_DATABASE_NAME = "MusicPlayerDatabase.db";
    /** 当前数据库版本 */
    private static final int MUSIC_DATABASE_VERSION = 3;

    private static final String TAG = Constants.TAG;

    public MusicDatabase(Context context) {
        super(context, MUSIC_DATABASE_NAME, null, MUSIC_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MusicTable.MUSIC_TABLE_NAME + " ( " +
                MusicTable.Cols.MUSIC_FIELD_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MusicTable.Cols.MUSIC_FIELD_PATH + " char(150) unique not null, " +
                MusicTable.Cols.MUSIC_FIELD_NAME + " char(80), " +
                MusicTable.Cols.MUSIC_FIELD_SINGER + " char(80), " +
                MusicTable.Cols.MUSIC_FIELD_INDEX + " char(80), " +
                MusicTable.Cols.MUSIC_FIELD_DURATION + " integer, " +
                MusicTable.Cols.MUSIC_FIELD_TIME + " TIMESTAMP DEFAULT(DATETIME('NOW','LOCALTIME')) );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseConstants.DROP_TABLE);
        Log.w(TAG, "MusicDatabase onUpgrade: ");
    }
}

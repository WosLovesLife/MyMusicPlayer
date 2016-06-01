package com.zhangheng.mymusicplayer.engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.project.myutilslibrary.CloseStreamTool;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.db.MusicDatabase;
import com.zhangheng.mymusicplayer.db.MusicDbSchema.MusicTable.Cols;
import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.global.DatabaseConstants;

import java.util.ArrayList;

/**
 * Created by zhangH on 2016/5/17.
 */
public class MusicDatabaseEngine {
    private static final String TABLE_NAME = DatabaseConstants.MUSIC_TABLE_NAME;
    private static final String TAG = Constants.TAG;

    private MusicDatabaseEngine() { }

    private static SQLiteDatabase getDB(Context context) {
        MusicDatabase musicDatabase = new MusicDatabase(context);
        return musicDatabase.getWritableDatabase();
    }

    public static ArrayList<MusicBean> readAll(Context context) {
        ArrayList<MusicBean> musicBeanArray = new ArrayList<>();
        SQLiteDatabase db = getDB(context);
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String path = cursor.getString(1);
                String name = cursor.getString(2);
                String singer = cursor.getString(3);
                String index = cursor.getString(4);
                int duration = cursor.getInt(5);
                musicBeanArray.add(new MusicBean(id, name, singer, path,index,duration));
            }
            CloseStreamTool.close(cursor);
        }
        return musicBeanArray;
    }

    public static void updateContent(Context context, ArrayList<MusicBean> musicBeanArray) {
        SQLiteDatabase db = getDB(context);
        ContentValues contentValues = new ContentValues();
        if (musicBeanArray != null && musicBeanArray.size() > 0) {
            truncateTable(context);
            for (MusicBean music :
                    musicBeanArray) {
                contentValues.put(Cols.MUSIC_FIELD_PATH,music.getPath());
                contentValues.put(Cols.MUSIC_FIELD_NAME,music.getMusicName());
                contentValues.put(Cols.MUSIC_FIELD_SINGER,music.getSinger());
                contentValues.put(Cols.MUSIC_FIELD_INDEX,music.getPinyin());
                contentValues.put(Cols.MUSIC_FIELD_DURATION,music.getDuration());
                long insert = db.insert(TABLE_NAME, DatabaseConstants.MUSIC_FIELD_KEY, contentValues);
                Log.w(TAG, "updateContent: 向数据库第"+insert+"行插入了新条目." );
                contentValues.clear();
            }
        }
    }

    private static void truncateTable(Context context) {
        SQLiteDatabase db = getDB(context);
        db.delete(DatabaseConstants.MUSIC_TABLE_NAME,null,null);
    }
}

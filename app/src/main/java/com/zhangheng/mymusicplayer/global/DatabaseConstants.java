package com.zhangheng.mymusicplayer.global;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zhangH on 2016/5/17.
 */
public interface DatabaseConstants {
    /** 数据库名 */
    String MUSIC_DATABASE_NAME = "MusicPlayerDatabase.db";
    /** 游标工厂,默认不需要 */
    SQLiteDatabase.CursorFactory MUSIC_DATABASE_CURSOR_FACTORY = null;
    /** 当前数据库版本 */
    int MUSIC_DATABASE_VERSION = 2;

    /** 第一版的表名 */
    String MUSIC_TABLE_NAME = "musics";

    /** 字段歌曲id */
    String MUSIC_FIELD_KEY = "_id";
    /** 歌曲才存储空间中的路径 */
    String MUSIC_FIELD_PATH = "path";
    /** 歌曲名 */
    String MUSIC_FIELD_NAME = "name";
    /** 歌手名 */
    String MUSIC_FIELD_SINGER = "singer";
    /** 添加时间 */
    String MUSIC_FIELD_TIME = "time";
    /** 第一版建表的执行语句 */
    String _SQL_MUSIC_TABLE_CREATE = "CREATE TABLE "+DatabaseConstants.MUSIC_TABLE_NAME+" ( "
            + DatabaseConstants.MUSIC_FIELD_KEY +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DatabaseConstants.MUSIC_FIELD_PATH +" CHAR(120) UNIQUE NOT NULL,"
            + DatabaseConstants.MUSIC_FIELD_NAME +" CHAR(40),"
            + DatabaseConstants.MUSIC_FIELD_SINGER +" CHAR(40),"
            + DatabaseConstants.MUSIC_FIELD_TIME +" TIMESTAMP DEFAULT(DATETIME('NOW','LOCALTIME')) );";

    String DROP_TABLE = "DROP TABLE "+DatabaseConstants.MUSIC_TABLE_NAME+";";

    /** 第二版表新增字段,歌曲名对应的字母索引 */
    String MUSIC_FIELD_INDEX = "pinyin";
    /** 第二版建表的执行语句 */
    String SQL_MUSIC_TABLE_CREATE = "CREATE TABLE "+DatabaseConstants.MUSIC_TABLE_NAME+" ( "
            + DatabaseConstants.MUSIC_FIELD_KEY +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DatabaseConstants.MUSIC_FIELD_PATH +" CHAR(120) UNIQUE NOT NULL,"
            + DatabaseConstants.MUSIC_FIELD_NAME +" CHAR(40),"
            + DatabaseConstants.MUSIC_FIELD_SINGER +" CHAR(40),"
            + DatabaseConstants.MUSIC_FIELD_INDEX+" CHAR(40),"
            + DatabaseConstants.MUSIC_FIELD_TIME +" TIMESTAMP DEFAULT(DATETIME('NOW','LOCALTIME')) );";
}

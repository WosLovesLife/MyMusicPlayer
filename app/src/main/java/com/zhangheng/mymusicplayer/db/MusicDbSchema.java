package com.zhangheng.mymusicplayer.db;

/**
 * Created by zhangH on 2016/6/1.
 */
public class MusicDbSchema {
    public static final class MusicTable{
        public static final String MUSIC_TABLE_NAME = "musics";

        public static final class Cols{
            /** 字段歌曲id */
            public static final String MUSIC_FIELD_KEY = "_id";
            /** 歌曲才存储空间中的路径 */
            public static final String MUSIC_FIELD_PATH = "path";
            /** 歌曲名 */
            public static final String MUSIC_FIELD_NAME = "name";
            /** 歌手名 */
            public static final String MUSIC_FIELD_SINGER = "singer";
            /** 添加时间 */
            public static final String MUSIC_FIELD_TIME = "time";

            /** 第二版表新增字段,歌曲名对应的字母索引 */
            public static final String MUSIC_FIELD_INDEX = "pinyin";

            /** 第三版新增字段,歌曲时长 */
            public static final String MUSIC_FIELD_DURATION = "duration";
        }
    }
}

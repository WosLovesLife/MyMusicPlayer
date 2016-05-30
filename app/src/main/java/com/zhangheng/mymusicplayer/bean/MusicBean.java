package com.zhangheng.mymusicplayer.bean;

import com.project.myutilslibrary.PinyinUtils;

import java.io.Serializable;

/**
 * Created by zhangH on 2016/4/30.
 */
public class MusicBean implements Serializable, Comparable<MusicBean> {

    private int mId;
    private String mMusicName;
    private String mSinger;
    private String mPath;
    private int duration;
    private String mPinyin;


    public MusicBean() {

    }

    public MusicBean(int id, String musicName, String singer, String path, String pinyin) {
        mId = id;
        mMusicName = musicName;
        mSinger = singer;
        mPath = path;
        mPinyin = pinyin;
    }

    public String getPinyin() {
        return mPinyin;
    }

    public void setPinyin(String pinyin) {
        mPinyin = pinyin;
    }

    public int getId() {
        return mId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public void setMusicName(String musicName) {
        mMusicName = musicName;
    }

    public String getSinger() {
        return mSinger;
    }

    public void setSinger(String singer) {
        mSinger = singer;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    @Override
    public String toString() {
        return "MusicBean{" +
                "mId=" + mId +
                ", mMusicName='" + mMusicName + '\'' +
                ", mSinger='" + mSinger + '\'' +
                ", mPath='" + mPath + '\'' +
                ", duration=" + duration +
                ", mPinyin='" + mPinyin + '\'' +
                '}';
    }

    @Override
    public int compareTo(MusicBean another) {
        return mPinyin.compareTo(another.getPinyin());
    }
}

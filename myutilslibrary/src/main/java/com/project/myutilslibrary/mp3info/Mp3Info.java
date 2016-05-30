package com.project.myutilslibrary.mp3info;

/**
 * Created by zhangH on 2016/5/30.
 */
public class Mp3Info {
    // 歌名
    private String mTit2;
    // 艺术家
    private String mTpe1;
    // 专辑
    private String mTalb;
    // 头像
    private byte[] mApic;

    public Mp3Info() {
    }

    public Mp3Info(String tit2, String tpe1, String talb, byte[] apic) {
        mTit2 = tit2;
        mTpe1 = tpe1;
        mTalb = talb;
        mApic = apic;
    }

    public String getTit2() {
        return mTit2;
    }

    public void setTit2(String tit2) {
        mTit2 = tit2;
    }

    public String getTpe1() {
        return mTpe1;
    }

    public void setTpe1(String tpe1) {
        mTpe1 = tpe1;
    }

    public String getTalb() {
        return mTalb;
    }

    public void setTalb(String talb) {
        mTalb = talb;
    }

    public byte[] getApic() {
        return mApic;
    }

    public void setApic(byte[] apic) {
        mApic = apic;
    }
}

package com.project.myutilslibrary.mp3info;

import android.app.Activity;
import android.graphics.Bitmap;

import com.project.myutilslibrary.CloseStreamTool;
import com.project.myutilslibrary.pictureloader.PictureScaleUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by WoslovesLife on 2016/5/30.
 * 本类用于获取ID3v2版本的音乐内嵌信息, 根据 技术博客作者 的原版修改而来
 * 原作信息:
 * Author 席有芳
 * QQ:951868171
 * Email xi_yf_001@126.com
 */
public class ReadID3v2 {

    public static final String CHARSET = "GBK";

    public static Bitmap getAlbumPicture(String filePath, Activity activity) {
        Bitmap picture = null;

        try {
            Mp3Info mp3Info = getMp3Info(filePath);

            picture = PictureScaleUtils.getScaledBitmap(mp3Info.getApic(),activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picture;
    }


    public static Mp3Info getMp3Info(String filePath) throws Exception {
        Mp3Info mp3Info = new Mp3Info();

        File file = new File(filePath);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        int buffSize = 100 * 1024;
        byte[] buff = new byte[buffSize];
        bis.read(buff, 0, buffSize);

        CloseStreamTool.close(bis);

        if (ByteUtil.indexOf("ID3".getBytes(), buff, 1, 512) == -1)
            throw new Exception("未发现ID3V2");

        //获取头像
        if (ByteUtil.indexOf("APIC".getBytes(), buff, 1, 512) != -1) {
            int searLen = ByteUtil.indexOf(new byte[]{(byte) 0xFF,
                    (byte) 0xFB}, buff);
            int imgStart = ByteUtil.indexOf(new byte[]{(byte) 0xFF,
                    (byte) 0xD8}, buff);
            int imgEnd = ByteUtil.lastIndexOf(new byte[]{(byte) 0xFF,
                    (byte) 0xD9}, buff, 1, searLen) + 2;
            byte[] imgb = ByteUtil.cutBytes(imgStart, imgEnd, buff);
            mp3Info.setApic(imgb);
        }

        if (ByteUtil.indexOf("TIT2".getBytes(), buff, 1, 512) != -1) {
            mp3Info.setTit2(new String(readInfo(buff, "TIT2"), CHARSET));
            System.out.println("info:" + mp3Info.getTit2());
        }

        if (ByteUtil.indexOf("TPE1".getBytes(), buff, 1, 512) != -1) {
            mp3Info.setTpe1(new String(readInfo(buff, "TPE1"), CHARSET));
            System.out.println("info:" + mp3Info.getTpe1());

        }

        if (ByteUtil.indexOf("TALB".getBytes(), buff, 1, 512) != -1) {
            mp3Info.setTalb(new String(readInfo(buff, "TALB"), CHARSET));
            System.out.println("info:" + mp3Info.getTalb());
        }

        return mp3Info;
    }

    /**
     * 读取文本标签
     **/
    private static byte[] readInfo(byte[] buff, String tag) {
        int len;
        int offset = ByteUtil.indexOf(tag.getBytes(), buff);
        len = buff[offset + 4] & 0xFF;
        len = (len << 8) + (buff[offset + 5] & 0xFF);
        len = (len << 8) + (buff[offset + 6] & 0xFF);
        len = (len << 8) + (buff[offset + 7] & 0xFF);
        len = len - 1;
        return ByteUtil.cutBytes(ByteUtil.indexOf(tag.getBytes(), buff) + 11,
                ByteUtil.indexOf(tag.getBytes(), buff) + 11 + len, buff);

    }
}

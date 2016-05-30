package com.project.myutilslibrary;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtils {

    public static String toPinyin(String name) {

        HanyuPinyinOutputFormat arg0 = new HanyuPinyinOutputFormat();
        arg0.setToneType(HanyuPinyinToneType.WITHOUT_TONE);    //设置不需要拼音
        arg0.setCaseType(HanyuPinyinCaseType.UPPERCASE); //设置返回大写字母
        char[] array = name.toCharArray();

        StringBuilder sBuilder = new StringBuilder();
        try {
            for (int i = 0; i < array.length; i++) {
                char c = array[i];
                Log.w("TAG", "c: " + c);
                if (c >= 0x30 && c <= 0x39) { //说明是数字, 不做处理
                    sBuilder.append(c + "");
                } else if (c >= 0x41 && c <= 0x5A) { //说明是大写字母, 不做处理
                    sBuilder.append(c + "");
                } else if (c >= 0x61 && c <= 0x7A) { //说明是小写字母,将其变为大写
                    sBuilder.append((c + "").toUpperCase());
                } else if (c > 0x4e00 && c <= 0x9fa5) { //说明是汉字, 将其转为拼音
                    String[] pinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(c, arg0);
                    sBuilder.append(pinyinStringArray[0]);    //如果该中文是一个多音词,会返回多个结果(所有读音).这里只取第一个.
                } else { //其他字符,不做识别,统一以 # 归类
                    sBuilder.append("#");
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return sBuilder.toString();
    }
}

package com.hammer.anlib.pandroidutils;

import java.text.DecimalFormat;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/3/2
 * Description: 书序工具类
 * Fix History:
 * =============================
 */
public class MathUtil {

    private static DecimalFormat f2point = new DecimalFormat("##0.00");

    /**
     * float获得两位小数点
     * @return
     */
    public static float getFloat2(float in){
        return Float.parseFloat(f2point.format(in));
    }

    public static float getAspectRatioValue(int width,int height){
        return (float)width / (float)height;
    }
}

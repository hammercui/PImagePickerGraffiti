package com.paicheya.pimagepicker.util;

import java.text.DecimalFormat;

/**
 * 数学工具
 * Created by cly on 17/2/9.
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

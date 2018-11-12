package com.example.watermark;

import javax.validation.constraints.NotNull;
import java.awt.*;

/**
 * Created by Zhangkh on 2018/11/12.
 */
public interface BaseWatermark {
    /**
     * 用平铺的方式插入水印
     *
     * @param srcFile       源文件，含路径
     * @param targetFile    输出文件，含路径
     * @param watermarkText 作为水印的文字
     * @throws Exception
     */
    void addWatermark(@NotNull String srcFile, String targetFile, @NotNull String watermarkText) throws Exception;

    /**
     * 一页只插入一个水印
     *
     * @param srcFile       源文件，含路径
     * @param targetFile    输出文件，含路径
     * @param watermarkText 作为水印的文字
     * @throws Exception
     */
    void addOneWatermark(@NotNull String srcFile, String targetFile, @NotNull String watermarkText) throws Exception;

    /**
     * 字体
     *
     * @param fontFamily
     */
    void setFont(String fontFamily);

    String getFont();

    /*
    字体大小
     */
    void setFontSize(float fontSize);

    float getFontSize();

    /**
     * 旋转角度
     *
     * @param rotation
     */
    void setRotation(int rotation);

    int getRotation();

    Color getColor();

    /**
     * 填充颜色
     *
     * @param color
     */
    void setColor(Color color);

    /**
     * 透明度
     *
     * @param opacity
     */
    void setOpacity(float opacity);

    float getOpacity();

}

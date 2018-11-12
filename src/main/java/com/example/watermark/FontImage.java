package com.example.watermark;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * Created by Zhangkh on 2018/11/7.
 */
public class FontImage {

    public static BufferedImage createImage(String text) {
        Font font = new Font("Times New Roman", Font.PLAIN, 25);
        int[] arr = getWidthAndHeight(text, font);
        int width = arr[0] + 5;
        int height = arr[1] + 5;
        // 创建图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);  //创建图片画布

        Graphics2D g = image.createGraphics();
        // 增加下面代码使得背景透明
        image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g.dispose();
        g = image.createGraphics();
        // 背景透明代码结束
//		g.fillRect(0, 0, width, height);//画出矩形区域，以便于在矩形区域内写入文字
        g.setColor(new Color(220, 220, 220));   // 再换成黑色，以便于写入文字
        g.setFont(font);   // 设置画笔字体

        g.translate(0, 10);

        g.drawString(text, 0, font.getSize());   // 画出一行字符串
        g.dispose();

        return image;
    }

    private static int[] getWidthAndHeight(String text, Font font) {
        Rectangle2D r = font.getStringBounds(text, new FontRenderContext(AffineTransform.getScaleInstance(1, 1), false, false));
        int unitHeight = (int) Math.floor(r.getHeight());
        // 获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
        int width = (int) Math.round(r.getWidth()) + 20;
        // 把单个字符的高度+3保证高度绝对能容纳字符串作为图片的高度
        int height = unitHeight + 20;
        return new int[]{width, height * 2};
    }
}

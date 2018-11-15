package com.example.commons.watermark;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import javax.validation.constraints.NotNull;

/**
 * Created by Zhangkh on 2018/11/7.
 */
public class ItextPdfWatermark implements BaseWatermark {

    private double width = 60;                               //图片的宽度
    private double height = 30;                              //图片的高度

    private int numPerLine = 3;                              //每行多少个水印
    private int numPerColumn = 6;                            //每列多少个水印

    private float opacity = 0.2f;                    //内部透明度
    //    private float strokeOpacity = 0.2f;          //边界线透明度
    private float fontSize = 20;                     //字体大小,单个水印大小为150，多个水印大小为20
    private int rotation = -45;                      //文字倾斜角度

    private String fontFamily = BaseFont.TIMES_ROMAN;   //字体
    private String fontEncode = "UTF-8";  //字体编码
    private Color color = Color.gray;    //文本颜色

    private int leftMargin = 60;                      //水印左边预留边距
    private int bottomMargin = 60;                    //水印底部预留边距
    private int rightMargin = 40;                     //水印右边预留边距
    private int topMargin = 60;                       //水印顶部预留边距

    private int textHorizontalGap = 80;               //文本水平间距
    private int textVerticalGap = 80;                 //文本上下间距

    private int beginPageNumber = 1;                //添加水印的起始页  默认从第一页开始
    private int endPageNumber = -1;                 //添加水印的起始页  默认为文件的最后一页

    public void addWatermark(String src, String target, String text, boolean onlyOne) throws IOException, DocumentException {
        PdfReader reader = null;
        PdfStamper pdfStamper = null;
        try {
            reader = new PdfReader(src);
            setRange(reader.getNumberOfPages());

            pdfStamper = new PdfStamper(reader, new FileOutputStream(target));

            if (onlyOne) {
                addOneWatermark(pdfStamper, text);
            } else {
                addPageWatermark(pdfStamper, text);
            }

        } finally {
            if (pdfStamper != null) {
                pdfStamper.close();
            }
        }
    }

    private void setRange(int total) {
        this.beginPageNumber = this.beginPageNumber < 1 || this.beginPageNumber > total ? 1 : this.beginPageNumber;
        this.endPageNumber = this.endPageNumber < beginPageNumber || this.endPageNumber > total ? total : this.endPageNumber;
    }

    //平铺整个页面
    private void addPageWatermark(PdfStamper pdfStamper, String watermark) throws DocumentException, IOException {
        PdfGState gs = new PdfGState();
        // 设置透明度为0.4
        gs.setFillOpacity(getOpacity());
        gs.setStrokeOpacity(getOpacity());

        // 设置字体
        BaseFont baseFont = BaseFont.createFont(getFont(), fontEncode, BaseFont.EMBEDDED);
        int toPage = pdfStamper.getReader().getNumberOfPages();
        PdfContentByte content = null;
        Rectangle pageRect = null;
        for (int i = 1; i <= toPage; i++) {
            pageRect = pdfStamper.getReader().getPageSizeWithRotation(i);
            //获得PDF最顶层
            content = pdfStamper.getOverContent(i);
            content.saveState();
            // set Transparency
            content.setGState(gs);
            content.beginText();
            content.setColorFill(new BaseColor(getColor().getRed(), getColor().getGreen(), getColor().getBlue()));
            content.setFontAndSize(baseFont, fontSize);

            float pageWidth = pageRect.getWidth();
            float pageHeight = pageRect.getHeight();

            //每个水印直接的间隔距离
            float gap = (float) (pageWidth - leftMargin - rightMargin - (numPerLine * width)) / (numPerLine - 1);
            float gapOfColumn = (float) (pageHeight - topMargin - bottomMargin - (numPerColumn * height)) / (numPerColumn - 1);

            float left = leftMargin;
            float top = topMargin;   //由低到高

            for (int x = 0; x < numPerLine; x++) {
                for (int y = 0; y < numPerColumn; y++) {
                    // 水印文字角倾斜
                    content.showTextAligned(Element.ALIGN_CENTER, watermark, left, top, getRotation());
                    top += height + gapOfColumn;
                }
                top = topMargin;
                left += width + gap;
            }

            content.endText();
        }
    }

    //一个页面只加一个水印
    private void addOneWatermark(PdfStamper pdfStamper, String watermark) throws DocumentException, IOException {
        PdfGState gs = new PdfGState();
        // 设置透明度
        gs.setFillOpacity(getOpacity());       //内部填充物透明度
        gs.setStrokeOpacity(getOpacity());     //边框透明度

        // 设置字体
        BaseFont baseFont = BaseFont.createFont(getFont(), fontEncode, BaseFont.EMBEDDED);

        int toPage = pdfStamper.getReader().getNumberOfPages();

        PdfContentByte content = null;
        Rectangle pageRect = null;
        for (int i = 1; i <= toPage; i++) {
            pageRect = pdfStamper.getReader().getPageSizeWithRotation(i);
            // 计算水印X,Y坐标
            float x = (pageRect.getLeft() + pageRect.getRight()) / 2;
            float y = (pageRect.getTop() + pageRect.getBottom()) / 2;
            //获得PDF最顶层
            content = pdfStamper.getOverContent(i);
            content.saveState();
            // set Transparency
            content.setGState(gs);
            content.beginText();
            content.setColorFill(new BaseColor(getColor().getRed(), getColor().getGreen(), getColor().getBlue()));
            content.setFontAndSize(baseFont, getFontSize());

            // 水印文字倾斜
            content.showTextAligned(Element.ALIGN_CENTER, watermark, x, y, getRotation());
            content.endText();
        }
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }


    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setFontEncode(String fontEncode) {
        this.fontEncode = fontEncode;
    }


    public int getBeginPageNumber() {
        return beginPageNumber;
    }

    public void setBeginPageNumber(int beginPageNumber) {
        this.beginPageNumber = beginPageNumber < 1 ? 1 : beginPageNumber;
    }

    public int getEndPageNumber() {
        return endPageNumber;
    }

    public void setEndPageNumber(int endPageNumber) {
        this.endPageNumber = endPageNumber < beginPageNumber ? -1 : endPageNumber;
    }


    @Override
    public void addWatermark(@NotNull String src, String target, @NotNull String watermarkText) throws Exception {
        long begin = System.currentTimeMillis();

        addWatermark(src, target, watermarkText, false);
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
        System.out.println("ok");
    }

    @Override
    public void addOneWatermark(String src, String target, String watermarkText) throws Exception {
        addWatermark(src, target, watermarkText, true);
    }

    @Override
    public void setFont(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public String getFont() {
        return this.fontFamily;
    }


    @Override
    public float getFontSize() {
        return this.fontSize;
    }

    @Override
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    @Override
    public int getRotation() {
        return this.rotation;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    @Override
    public float getOpacity() {
        return this.opacity;
    }
}

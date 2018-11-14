package com.example.commons.watermark;

import com.aspose.words.*;
import com.aspose.words.Shape;
import com.example.util.office.OfficeUtil;

import java.awt.*;


public class AsposeWordWatermarkPic implements BaseWatermark {

    private double width = 60;                               //图片的宽度
    private double height = 20;                              //图片的高度

    private int numPerLine = 5;                              //每行多少个水印
    private String font = "Times New Roman";                //字体
    private float fontSize = 20;                             //字体大小
    private int rotation = 40;                               //旋转角度
    private Color color = Color.gray;
    private float opacity = 0.5f;                            //透明度


    /**
     * Inserts a watermark into a document.
     *
     * @param doc           The input document.
     * @param watermarkText Text of the watermark.
     */
    //ExStart:insertWatermarkText
    private void addWatermark(Document doc, String watermarkText) throws Exception {

        // Insert the watermark into all headers of each document section.
        for (Section sect : doc.getSections()) {
            // There could be up to three different headers in each section, since we want
            // the watermark to appear on all pages, insert into all headers.
            Paragraph watermarkPara = new Paragraph(doc);
            double leftMargin = sect.getPageSetup().getLeftMargin();
            double topMargin = sect.getPageSetup().getTopMargin() + 30;
            double rightMargin = sect.getPageSetup().getRightMargin();
            double bottomMargin = sect.getPageSetup().getBottomMargin() + 15;
            double pageWidth = sect.getPageSetup().getPageWidth();
            double pageHeight = sect.getPageSetup().getPageHeight();

            //每个水印直接的间隔距离
            float gap = (float) (pageWidth - leftMargin - rightMargin - (numPerLine * width)) / (numPerLine - 1);

            double left = leftMargin;
            double top = topMargin;
            for (int i = 0; i < numPerLine; i++) {
                for (double j = topMargin; j < pageHeight - bottomMargin; ) {
                    watermarkPara.appendChild(getShape(doc, watermarkText, left, j));
                    j += 80;
                }
//                System.out.println(i);
                left = left + (gap + width);
            }


            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_PRIMARY);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_FIRST);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_EVEN);
        }
    }

    private void insertOneWatermarkText(Document doc, String watermarkText) throws Exception {
        // Insert the watermark into all headers of each document section.
        Paragraph watermarkPara = new Paragraph(doc);
        watermarkPara.appendChild(getShape(doc, watermarkText, true));
        for (Section sect : doc.getSections()) {
            // There could be up to three different headers in each section, since we want
            // the watermark to appear on all pages, insert into all headers.

            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_PRIMARY);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_FIRST);
            insertWatermarkIntoHeader(watermarkPara, sect, HeaderFooterType.HEADER_EVEN);
        }
    }

    private Shape getShape(Document doc, String watermarkText, double left, double top) throws Exception {
        return getShape(doc, watermarkText, left, top, false);
    }

    private Shape getShape(Document doc, String watermarkText, boolean isOne) throws Exception {
        width = 500;
        height = 100;

        return getShape(doc, watermarkText, 0, 0, false);
    }

    private Shape getShape(Document doc, String watermarkText, double left, double top, boolean isOne) throws Exception {
        // Create a watermark shape. This will be a WordArt shape.
        // You are free to try other shape types as watermarks.
        Shape watermark = new Shape(doc, ShapeType.TEXT_PLAIN_TEXT);
        // Set name to be able to remove it afterwards
        watermark.setName("WaterMark");

//        watermark.getImageData().setImage(FontImage.createImage(watermarkText));


        // Set up the text of the watermark.
//        watermark.getTextBox().setFitShapeToText(true);
        watermark.getTextPath().setText(watermarkText);
        watermark.getTextPath().setFontFamily(getFont());
        watermark.getTextPath().setSize(getFontSize());
        watermark.getTextPath().setFitPath(false);
        watermark.getTextPath().setFitShape(false);   //文字不随图形大小改变
        watermark.setWidth(width);
        watermark.setHeight(height);
//        watermark.setStroked(false);


        // Text will be directed from the bottom-left to the top-right corner.
        watermark.setRotation(getRotation());
        // Remove the following two lines if you need a solid black text.
//        watermark.getFill().setColor(getColor()); // Try LightGray to get more Word-style watermark
//        watermark.setStrokeColor(getColor()); // Try LightGray to get more Word-style watermark

        // Place the watermark in the page center.
        watermark.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
        watermark.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
        watermark.setWrapType(WrapType.NONE);
        watermark.setBehindText(true);   //水印在文字之上

        watermark.getFill().setOpacity(getOpacity());   //设置透明杜
        watermark.getStroke().setOpacity(getOpacity());

        if (isOne) {
            watermark.setVerticalAlignment(VerticalAlignment.CENTER);
            watermark.setHorizontalAlignment(HorizontalAlignment.CENTER);
        } else {
            watermark.setLeft(left);
            watermark.setTop(top);
        }

        return watermark;

    }

    private static void insertWatermarkIntoHeader(Paragraph watermarkPara, Section sect, int headerType) throws Exception {
        HeaderFooter header = sect.getHeadersFooters().getByHeaderFooterType(headerType);
        if (header == null) {
            // There is no header of the specified type in the current section, create it.
            header = new HeaderFooter(sect.getDocument(), headerType);
            sect.getHeadersFooters().add(header);
        }

        // Insert a clone of the watermark into the header.
        header.appendChild(watermarkPara.deepClone(true));
    }

    public void addWatermark(String src, String target, String watermarkText, boolean isOne) throws Exception {
        OfficeUtil.getLicense();
        long begin = System.currentTimeMillis();
        Document doc = new Document(src);
        if (isOne) {
            insertOneWatermarkText(doc, watermarkText);
        } else {
            //平铺
            addWatermark(doc, watermarkText);
        }

//        doc.protect(ProtectionType.READ_ONLY);
        SaveOptions options = SaveOptions.createSaveOptions(SaveFormat.DOCX);
        doc.save(target, options);
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }

    @Override
    public void addWatermark(String src, String target, String watermarkText) throws Exception {
        addWatermark(src, target, watermarkText, false);
    }

    @Override
    public void addOneWatermark(String src, String target, String watermarkText) throws Exception {
        addWatermark(src, target, watermarkText, true);
    }

    @Override
    public void setFont(String fontFamily) {
        this.font = fontFamily;

    }

    @Override
    public String getFont() {
        return this.font;
    }

    @Override
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public float getFontSize() {
        return fontSize;
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
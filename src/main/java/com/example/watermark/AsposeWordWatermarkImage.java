package com.example.watermark;

import com.aspose.words.*;
import com.aspose.words.Shape;
import com.example.util.office.OfficeUtil;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Zhangkh on 2018/11/7.
 */
public class AsposeWordWatermarkImage {

    /**
     * Inserts a watermark into a document.
     *
     * @param doc           The input document.
     * @param watermarkText Text of the watermark.
     */
    public static void insertWatermarkText(Document doc, String watermarkText) throws Exception {
        float width = doc.getPageInfo(1).getWidthInPoints();
        float height = doc.getPageInfo(1).getHeightInPoints();
        System.out.println("width:" + width);
        System.out.println("sec:" + doc.getSections().getCount());
        // Insert the watermark into all headers of each document section.

        DocumentBuilder builder = new DocumentBuilder(doc);

        NodeCollection nodeCollection = doc.getChildNodes(NodeType.BODY, true);
        System.out.println("node count:" + nodeCollection.getCount());
        LayoutCollector layoutCollector = new LayoutCollector(doc);

        BufferedImage bufferedImage = FontImage.createImage(watermarkText);


        Set<Integer> donePage = new HashSet<Integer>();
        // Insert the watermark into all headers of each document section.
        for (Section sect : doc.getSections()) {
            System.out.println("child node:" + sect.getBody().getChildNodes().getCount());
//            sect.getPageSetup().
            for (int k = 0; k < sect.getBody().getChildNodes().getCount(); k++) {

                ParagraphCollection paragraphCollection = sect.getBody().getParagraphs();

                Node node = sect.getBody().getChildNodes().get(k);
                if (NodeType.HEADER_FOOTER == node.getNodeType()) {
                    //跳过页眉页脚
                    continue;
                }
                int pageNum = layoutCollector.getStartPageIndex(node);
                if (donePage.contains(pageNum)) {
                    continue;
                }
                int cnt = layoutCollector.getNumPagesSpanned(node);
                System.out.println(cnt);
                builder.moveTo(node);
//                System.out.println(pageNum);
                donePage.add(pageNum);

                for (int i = 50; i < width - 60; ) {
                    for (int j = 50; j < height - 100; ) {
                        addImage(builder, i, j, bufferedImage);
                        j += 100;
                    }
                    i += 100;
                }

            }


        }

//        doc.getCount();
        System.out.println("pagenumber:" + doc.getPageCount());
    }

    static void addImage(DocumentBuilder builder, double left, double top, BufferedImage bufferedImage) throws Exception {
        Shape shape = builder.insertImage(bufferedImage);
//        Shape shape = builder.insertImage(
//                bufferedImage,
//                RelativeHorizontalPosition.PAGE,
//                0,
//                RelativeVerticalPosition.PAGE,
//                0,
//                0,
//                0,
//                WrapType.NONE);

        shape.setRelativeHorizontalPosition(RelativeHorizontalPosition.PAGE);
        shape.setRelativeVerticalPosition(RelativeVerticalPosition.PAGE);
        shape.setWrapType(WrapType.NONE);
        shape.setBehindText(false);

        shape.setLeft(left);
        shape.setTop(top);
//        shape.setWidth(bufferedImage.getWidth());
//        shape.setHeight(bufferedImage.getHeight());
        shape.setRotation(30);

    }


    static void test() {
        //ExStart:AddWatermarkToADocument
        String fileName = "G:\\test\\中泰证券风控合规门户需求规格说明书V4.1.docx";
        String newFileName = "G:\\test\\中泰证券风控合规门户需求规格说明书V4.1-1.docx";
        Document doc = null;
        OfficeUtil.getLicense();
        long begin = System.currentTimeMillis();
        try {
            doc = new Document(fileName);
            insertWatermarkText(doc, "zhangkh");
            SaveOptions saveOptions = SaveOptions.createSaveOptions(11);
//            doc.protect(1);
            doc.save(newFileName, saveOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - begin);

    }


    public static void main(String[] args) throws Exception {
//        createImage("zhangkh");
        test();
    }
}

package com.example.util.office;

import com.aspose.words.Document;

import java.io.File;


/**
 * Created by Zhangkh on 2018/3/29.
 */
public class Doc2PDF {

    public static void test(String[] args) throws Exception {
        // The path to the documents directory.
//        String dataDir = Utils.getDataDir(Doc2PDF.class);

        String dataDir = "C:\\Users\\Zhangkh\\Desktop\\";

        //ExStart:Doc2Pdf
        // Load the document from disk.
        Document doc = new Document(dataDir + "移动办公附件说明.docx");

        // Save the document in PDF format.
        dataDir = dataDir + "13output.pdf";
        doc.save(dataDir);
        //ExEnd:Doc2Pdf

        System.out.println("\nDocument converted to PDF successfully.\nFile saved at " + dataDir);
    }



    public static void main(String[] args) throws Exception {
        String targetFile = "F:\\04 集成信息管理平台\\待办中心\\接口文档\\常用字典项.pdf";
//        String a = new File(targetFile).getParentFile().getAbsolutePath();
        int idx = targetFile.lastIndexOf(".");

        String suffix = targetFile.substring(idx + 1);
        System.out.println(suffix);

    }
}

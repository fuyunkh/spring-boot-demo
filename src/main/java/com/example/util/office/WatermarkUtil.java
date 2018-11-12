package com.example.util.office;

import com.example.watermark.AsposeWordWatermark;
import com.example.watermark.BaseWatermark;
import com.example.watermark.ItextPdfWatermark;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by Zhangkh on 2018/11/12.
 */
public class WatermarkUtil {
    public static void process(String srcFile, String targetFile, String watermarkText) throws Exception {
        BaseWatermark processor = null;
        File file = new File(srcFile);
        if (FileExtensionUtils.isWord(file.getName())) {
            processor = new AsposeWordWatermark();
        } else if (FileExtensionUtils.isPdf(file.getName())) {
            processor = new ItextPdfWatermark();
        }
        if (processor != null) {
            processor.addWatermark(srcFile, targetFile, watermarkText);
        } else {
            throw new Exception("不支持文件格式为 " + FilenameUtils.getExtension(file.getName()) + " 的水印处理");
        }
    }

    public static void main(String[] args) throws Exception {

        String srcFile = "G:\\test\\aa\\软件学院-张开会-201313363-18560131140 打印版.docx";
        String target = "G:\\test\\aa\\软件学院-张开会-201313363-18560131140 打印版-1.docx";
        WatermarkUtil.process(srcFile, target, "zhangkh");
    }
}

package com.example.util.office;

import com.aspose.cells.PdfSecurityOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.pdf.*;
import com.aspose.words.*;
import com.aspose.words.Document;
//import com.aspose.words.License;
//import com.aspose.words.PdfSaveOptions;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Zhangkh on 2018/3/29.
 */
public class OfficeUtil {

    protected static final Logger logger = LoggerFactory.getLogger(OfficeUtil.class);

    private static boolean allowPrint = true;   //是否允许打印
    private static String passwd = "123456";    //加密密码
    private static String licenseFile = "";    //授权文件
    private static String fontPath = "";    //字体路径
    static CaseInsensitiveMap mapping = new CaseInsensitiveMap();

    static {
        mapping.put("doc", "WORD");
        mapping.put("wps", "WORD");
        mapping.put("docx", "WORD");

        mapping.put("xls", "EXCEL");
        mapping.put("xlsx", "EXCEL");

        mapping.put("csv", "EXCEL");
        mapping.put("et", "EXCEL");

        mapping.put("pdf", "PDF");
    }

    public static void setConfig(boolean allowPrint, String passwd, String license, String fontPath) {
        OfficeUtil.allowPrint = allowPrint;
        OfficeUtil.passwd = passwd;
        OfficeUtil.licenseFile = license;
        OfficeUtil.fontPath = fontPath;
    }

    public static boolean getLicense() {
        boolean result = false;
        try {
            String fileName = isNullOrEmpty(licenseFile) ? "properties/license.xml" : licenseFile;

            InputStream isWord = OfficeUtil.class.getClassLoader()
                    .getResourceAsStream(fileName);
            com.aspose.words.License wordLic = new com.aspose.words.License();
            wordLic.setLicense(isWord);

            InputStream isCell = OfficeUtil.class.getClassLoader()
                    .getResourceAsStream(fileName);
            com.aspose.cells.License cellLic = new com.aspose.cells.License();
            cellLic.setLicense(isCell);

            InputStream pdf = OfficeUtil.class.getClassLoader()
                    .getResourceAsStream(fileName);
            com.aspose.pdf.License pdfLic = new com.aspose.pdf.License();
            pdfLic.setLicense(pdf);

            result = true;
        } catch (Exception e) {
            logger.error("getLicense", e);
        }
        return result;
    }

    private static boolean allowPrint() {
        return allowPrint;
    }

    private static String getPasswd() {
        return isNullOrEmpty(passwd) ? "123456" : passwd;
    }


    public static void excel2Pdf(String srcFile, String targetFile) throws Exception {
        if (!getLicense()) {
            return;
        }

        setFont(fontPath);
        Workbook workbook = null;
        try {
            workbook = new Workbook(srcFile);
        } catch (Exception err) {
            logger.error("excel2pdf===>无法识别的Excel文件！", err);
            throw new Exception("无法识别的Excel文件！");
        }

        for (int i = 0; i < workbook.getWorksheets().getCount(); ++i) {
            Worksheet sheet = workbook.getWorksheets().get(i);
            com.aspose.cells.PageSetup pageSetup = sheet.getPageSetup();
            pageSetup.setFitToPagesWide(1);
            pageSetup.setFitToPagesTall(0);
        }

        com.aspose.cells.PdfSaveOptions opts = new com.aspose.cells.PdfSaveOptions();
        if (!allowPrint()) {
            String password = getPasswd();
            PdfSecurityOptions pdfSecurityOptions = new PdfSecurityOptions();
            pdfSecurityOptions.setAnnotationsPermission(false);
            pdfSecurityOptions.setAssembleDocumentPermission(false);
            pdfSecurityOptions.setExtractContentPermission(false);
            pdfSecurityOptions.setExtractContentPermissionObsolete(false);
            pdfSecurityOptions.setFillFormsPermission(false);
            pdfSecurityOptions.setFullQualityPrintPermission(false);
            pdfSecurityOptions.setModifyDocumentPermission(false);
            pdfSecurityOptions.setOwnerPassword(password);
            pdfSecurityOptions.setPrintPermission(false);
            opts.setSecurityOptions(pdfSecurityOptions);
        }
        workbook.save(targetFile, opts);

    }

    public static void doc2Pdf(String srcFile, String targetFile) throws Exception {
        if (!getLicense()) {
            return;
        }
        long old = System.currentTimeMillis();
        setFont(fontPath);
        Document doc = null;
        try {
            doc = new Document(srcFile);
        } catch (Exception err) {
            logger.error("doc2pdf===>无法识别的Word文件！", err);
            throw new Exception("无法识别的Word文件！");
        }

        com.aspose.words.PdfSaveOptions opts = new com.aspose.words.PdfSaveOptions();
        //不允许打印时加密
        if (!allowPrint()) {
            String password = getPasswd();
            PdfEncryptionDetails encryptionDetails = new PdfEncryptionDetails("", password, 1);
            encryptionDetails.setPermissions(16);
            opts.setEncryptionDetails(encryptionDetails);
        }

        RevisionOptions rOpts = doc.getLayoutOptions().getRevisionOptions();
        rOpts.setShowOriginalRevision(false);
        rOpts.setShowRevisionBalloons(false);
        rOpts.setShowRevisionBars(false);
        rOpts.setShowRevisionMarks(false);

        double top = 500.0D;
        double left = 500.0D;
        double right = 500.0D;
        double bottom = 500.0D;

        for (Section sec : doc.getSections()) {
            if (sec.getPageSetup().getTopMargin() <= top) {
                top = sec.getPageSetup().getTopMargin();
            }

            if (sec.getPageSetup().getRightMargin() <= right) {
                right = sec.getPageSetup().getRightMargin();
            }

            if (sec.getPageSetup().getLeftMargin() <= left) {
                left = sec.getPageSetup().getLeftMargin();
            }

            if (sec.getPageSetup().getBottomMargin() <= bottom) {
                bottom = sec.getPageSetup().getBottomMargin();
            }

        }

        left -= 8.0D;
        right -= 8.0D;

        for (Section sec : doc.getSections()) {
            sec.getPageSetup().setBottomMargin(bottom);
            sec.getPageSetup().setLeftMargin(left);
            sec.getPageSetup().setTopMargin(top);
            sec.getPageSetup().setRightMargin(right);
        }
        doc.save(targetFile, opts);
        long now = System.currentTimeMillis();
        logger.info("word2pdf共耗时：" + ((now - old)) + "ms");  //转化用时
    }

    public static void image2Pdf(String srcFile, String targetFile) throws Exception {
        if (!getLicense()) {
            return;
        }

        try {
            Document doc = new Document();
            DocumentBuilder builder = new DocumentBuilder(doc);
            com.aspose.words.PageSetup pageSetup = builder.getPageSetup();
            pageSetup.setOrientation(2);
            pageSetup.setLeftMargin(0.0D);
            pageSetup.setRightMargin(0.0D);
            pageSetup.setTopMargin(0.0D);
            pageSetup.setBottomMargin(0.0D);

            Shape shape = builder.insertImage(srcFile);
            double shapeWidth = shape.getWidth();
            double shapeHeight = shape.getHeight();

            pageSetup.setPageWidth(shapeWidth);
            pageSetup.setPageHeight(shapeHeight);
            doc.save(targetFile);

        } catch (Exception ex) {
            logger.error("转换图片文件到pdf格式失败", ex);
        } finally {

        }
    }

    public static void doc2Docx(String srcFile, String targetFile) throws Exception {
        if (!getLicense()) {
            return;
        }

        try {
            setFont(fontPath);
            Document doc = new Document(srcFile);

            doc.removeMacros();
            doc.save(targetFile, 20);
        } catch (Exception err) {
            logger.error("无法识别的doc文件！", err);
            throw new Exception("无法识别的doc文件！");
        }
    }

    //设置自定义字体路径
    public static void setFont(String fontPath) throws Exception {
        if (!isNullOrEmpty(fontPath)) {
            FontSettings.getDefaultInstance().setFontsFolder(fontPath, false);
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0; // string.isEmpty() in Java 6
    }

    public static void file2Pdf(String srcFile, String targetFile) throws Exception {
        if (isNullOrEmpty(srcFile) || isNullOrEmpty(targetFile)) {
            logger.error("源文件、目标文件不能为空");
            throw new Exception("源文件、目标文件不能为空");
        }

        int idx = srcFile.lastIndexOf(".");
        if (idx <= 0) {
            logger.error("文件名必须有后缀");
            throw new Exception("文件名必须有后缀");
        }
        String suffix = srcFile.substring(idx + 1);

        //生成目标路径
        File target = new File(targetFile);
        String targetPath = target.getParentFile().getAbsolutePath();
        FileUtils.forceMkdir(new File(targetPath));

        switch (getFileType(suffix)) {
            case "WORD":
                if (suffix.equalsIgnoreCase("doc") || suffix.equalsIgnoreCase("wps")) {
                    String tempFile = targetFile.substring(0, targetFile.lastIndexOf(".")) + ".docx";
                    doc2Docx(srcFile, tempFile);
                    doc2Pdf(tempFile, targetFile);
                    FileUtils.forceDelete(new File(tempFile));
                } else {
                    doc2Pdf(srcFile, targetFile);
                }
                break;
            case "EXCEL":
                excel2Pdf(srcFile, targetFile);
                break;
            case "PDF":
                FileUtils.copyFile(new File(srcFile), new File(targetFile));
                break;
            default:
        }
    }

    public static void addPdfWatermark() {
        if (!getLicense()) {
            return;
        }

        String file = "G:\\test\\01-表单定义培训.pdf";
        // open document
        com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(file);
        // create text stamp
        TextStamp textStamp = new TextStamp("zhangkh");
        // set whether stamp is background
        textStamp.setBackground(true);
        // set origin
        textStamp.setXIndent(100);
        textStamp.setYIndent(100);
        // rotate stamp
        textStamp.setRotate(Rotation.on90);
        // set text properties
        textStamp.getTextState().setFont(new FontRepository().findFont("Arial"));
        textStamp.getTextState().setFontSize(14.0F);
        textStamp.getTextState().setFontStyle(FontStyles.Bold);
        textStamp.getTextState().setFontStyle(FontStyles.Italic);
        textStamp.getTextState().setForegroundColor(Color.getGreen());
        // add stamp to particular page
        pdfDocument.getPages().get_Item(1).addStamp(textStamp);


        // ExStart:InfoClass
        // iterate through all pages of PDF file
        for (int i = 1; i <= pdfDocument.getPages().size(); i++) {
            // add stamp to all pages of PDF file
            pdfDocument.getPages().get_Item(i).addStamp(textStamp);
        }
        // ExEnd:InfoClass

        // save output document
        pdfDocument.save("G:\\test\\01-表单定义培训-3.pdf");

    }

    public static String getFileType(String suffix) {
        return String.valueOf(mapping.get(suffix));
    }

    public static void main(String[] args) {
        String srcFile = "G:\\QQ Files\\user_info_OLD.csv";
        String targetFile = "G:\\QQ Files\\user_info_OLD.pdf";
        try {
            doc2Pdf(srcFile, targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

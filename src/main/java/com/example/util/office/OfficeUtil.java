package com.example.util.office;

import com.aspose.cells.FontConfigs;
import com.aspose.cells.PdfSecurityOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.words.*;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by Zhangkh on 2018/3/29.
 */
public class OfficeUtil {

    protected final static Logger logger = LoggerFactory.getLogger(OfficeUtil.class);

    private static boolean allowPrint = true;   //是否允许打印
    private static String passwd = "123456";    //加密密码

    public static void setConfig(boolean allowPrint, String passwd) {
        OfficeUtil.allowPrint = allowPrint;
        OfficeUtil.passwd = passwd;
    }

    private static boolean getLicense() {
        boolean result = false;
        try {
            String fileName = "properties/license.xml";

            InputStream isWord = OfficeUtil.class.getClassLoader()
                    .getResourceAsStream(fileName);
            com.aspose.words.License wordLic = new com.aspose.words.License();
            wordLic.setLicense(isWord);

            InputStream isCell = OfficeUtil.class.getClassLoader()
                    .getResourceAsStream(fileName);
            com.aspose.cells.License cellLic = new com.aspose.cells.License();
            cellLic.setLicense(isCell);

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
        return Strings.isNullOrEmpty(passwd) ? "123456" : passwd;
    }

    public static void excel2Pdf(String srcFile, String targetFile) throws Exception {
        excel2Pdf(srcFile, targetFile, null);
    }

    public static void excel2Pdf(String srcFile, String targetFile, String fontPath) throws Exception{
        if (!getLicense()) {
            return;
        }

        setExcelFont(fontPath);
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
        doc2Pdf(srcFile, targetFile, null);
    }

    public static void doc2Pdf(String srcFile, String targetFile, String fontPath) throws Exception {
        if (!getLicense()) {
            return;
        }
        long old = System.currentTimeMillis();
        setDocFont(fontPath);
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
        doc2Docx(srcFile, targetFile, null);
    }

    public static void doc2Docx(String srcFile, String targetFile, String fontPath) throws Exception {
        if (!getLicense()) {
            return;
        }

        try {
            setDocFont(fontPath);
            Document doc = new Document(srcFile);
            doc.removeMacros();
            doc.save(targetFile, 20);
        } catch (Exception err) {
            logger.error("无法识别的doc文件！", err);
            throw new Exception("无法识别的doc文件！");
        }
    }

    //设置自定义字体路径
    public static void setDocFont(String fontPath) throws Exception {
        if (!Strings.isNullOrEmpty(fontPath)) {
            FontSettings.getDefaultInstance().setFontsFolder(fontPath, false);
        }
    }

    public static void setExcelFont(String fontPath) {
        if (!Strings.isNullOrEmpty(fontPath)) {
//            CellsHelper.setFontDir(pdfFontPath);
            FontConfigs.setFontFolder(fontPath, false);
        }
    }

    public static void main(String[] args) {
        String srcFile = "G:\\QQ Files\\livebos控件类型映射对比.xls";
        String targetFile = "G:\\QQ Files\\livebos控件类型映射对比.pdf";
        try {
            excel2Pdf(srcFile, targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

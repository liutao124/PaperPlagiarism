package cn.octopus.paperplagiarism.util;

import cn.hutool.core.io.FileUtil;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.File;
import java.io.FileInputStream;

public class WordUtil {

    private static final String DOC = "doc";
    private static final String DOCX = "docx";
    private static final String WPS = "wps";


    public static String readWord(File file) {
        String fileText = "";
        try {
            if (FileUtil.extName(file).equals(DOC) || FileUtil.extName(file).equals(WPS)) {
                FileInputStream is = new FileInputStream(file);
                WordExtractor ex = new WordExtractor(is);
                fileText = ex.getText();
                is.close();
            } else if (FileUtil.extName(file).endsWith(DOCX)) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(file.getPath());
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                fileText = extractor.getText();
                opcPackage.close();
            } else {
                System.out.println("word工具类：此文件不是word文件！");
            }

        } catch (Exception e) {
            System.out.println("word文件'"+file.getPath()+"'读取异常！异常原因："+e);
        }
        //new
        return fileText;
    }


}

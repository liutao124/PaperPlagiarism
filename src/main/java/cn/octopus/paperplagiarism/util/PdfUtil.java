/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cn.octopus.paperplagiarism.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfUtil {

    /**
     * 读取 PDF 文件的内容
     *
     * @param file PDF 文件
     * @return 文件内容
     */
    public static String readPdf(File file) {
        String fileText = "";
        try (PDDocument document = PDDocument.load(file)) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                fileText = stripper.getText(document);
            }
        } catch (IOException e) {
            System.out.println("pdf文件'"+file.getPath()+"'读取异常！异常原因："+e);
        }
        return fileText;
    }
}

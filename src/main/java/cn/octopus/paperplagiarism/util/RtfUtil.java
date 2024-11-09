package cn.octopus.paperplagiarism.util;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RtfUtil {
    /**
     * 读取 RTF 文件的内容
     *
     * @param file RTF 文件
     * @return 文件内容
     */
    public static String readRtf(File file) {
        String fileText = "";
        try (FileInputStream fis = new FileInputStream(file)) {
            DefaultStyledDocument styledDoc = new DefaultStyledDocument();
            new RTFEditorKit().read(fis, styledDoc, 0);
            //解决编码问题
            fileText = styledDoc.getText(0, styledDoc.getLength());
        } catch (IOException | BadLocationException e) {
            System.out.println("rtf文件'"+file.getPath()+"'读取异常！异常原因："+e);
        }
        return fileText;
    }
}

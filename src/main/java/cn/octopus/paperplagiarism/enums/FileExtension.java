/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cn.octopus.paperplagiarism.enums;

/**
 *
 * @author taoliu
 */
public enum FileExtension {  
    DOCX("docx"), XLSX("xlsx"), PDF("pdf"), DOC("doc"), XLS("xls");  
  
    private final String extension;  
  
    FileExtension(String extension) {  
        this.extension = extension;  
    }  
  
    public String getExtension() {  
        return extension;  
    }  
  
    // 静态方法来判断给定的扩展名是否有效  
    public static boolean isValidExtension(String extension) {  
        for (FileExtension fileExtension : FileExtension.values()) {  
            if (fileExtension.getExtension().equals(extension)) {  
                return true;  
            }  
        }  
        return false;  
    }  
}
package cn.octopus.paperplagiarism.util;

import cn.octopus.paperplagiarism.vo.FileData;
import java.io.File;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportUtil {

    public static void toExcel(List<FileData> dataList, File filePath) {
        Workbook workbook = new XSSFWorkbook(); // 创建一个Excel工作簿
        Sheet sheet = workbook.createSheet("文件对比重复数据"); // 创建一个工作表

        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("序号");
        headerRow.createCell(1).setCellValue("源文件路径");
        headerRow.createCell(2).setCellValue("对比文件路径");
        headerRow.createCell(3).setCellValue("重复率");

        // 遍历数据并写入
        int reqNum = 1;
        int rowIndex = 1;
        for (FileData fileData : dataList) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(reqNum++);
            row.createCell(1).setCellValue(fileData.getFilePath1());
            row.createCell(2).setCellValue(fileData.getFilePath2());
            row.createCell(3).setCellValue(fileData.getPercent());
        }

        // 自动调整列宽  
        for (int colNum = 0; colNum < 4; colNum++) {  
            sheet.autoSizeColumn(colNum);  
        }  
        
        // 将Excel写入文件
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

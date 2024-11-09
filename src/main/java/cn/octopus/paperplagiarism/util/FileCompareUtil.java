package cn.octopus.paperplagiarism.util;

import cn.hutool.core.io.FileUtil;
import cn.octopus.paperplagiarism.enums.FileExtension;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class FileCompareUtil {
    /**
     *
     * @param simHashesDoc1
     *              需要对比的文件
     * @param simHashesDoc2
     *              对比参照文件
     *
     * @return  如果文档的相似度大于相似度阈值返回 true，否则返回 false
     */
    public static BigDecimal compareFiles(List<SimHash> simHashesDoc1, List<SimHash> simHashesDoc2){
        int similarSentencesCount = 0; // 相似句子数
        for (SimHash hash1 : simHashesDoc1) {
            boolean isSimilarFound = false;
            for (SimHash hash2 : simHashesDoc2) {
                int hammingDistance = hash1.hammingDistance(hash2);
                if (hammingDistance < 10) {
                    isSimilarFound = true;
                    break;
                }
            }
            if (isSimilarFound) {
                similarSentencesCount++;
            }
        }
        if(simHashesDoc1.size() == 0){
            return BigDecimal.ZERO;
        }else{
            return new BigDecimal(similarSentencesCount)
                    .divide(new BigDecimal(simHashesDoc1.size()), 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static File[] getValidFiles(String fileDir) {
        List<File> files = FileUtil.loopFiles(fileDir);
        if (files == null) {
            // 如果 loopFiles 理论上不应该返回 null，这里可以抛出一个异常或记录错误
            throw new IllegalArgumentException("Provided directory path is invalid or loopFiles method returned null.");
        }
        // 使用 Stream API 过滤出有效的文件
        return files.stream()
                .filter(file -> !file.getName().startsWith(".") && FileExtension.isValidExtension(FileUtil.extName(file)))
                .toArray(File[]::new);
    }
}

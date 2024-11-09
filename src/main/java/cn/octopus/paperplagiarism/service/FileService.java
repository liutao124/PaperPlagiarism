package cn.octopus.paperplagiarism.service;

import cn.hutool.core.io.FileUtil;
import cn.octopus.paperplagiarism.controller.FileController;
import cn.octopus.paperplagiarism.util.*;
import cn.octopus.paperplagiarism.vo.FileData;
import cn.octopus.paperplagiarism.vo.InitConfigUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.io.File;

import java.math.BigDecimal;
import java.util.List;

import java.util.Map;

public class FileService implements Job {

    private static final FileController fileController = ObjectInstUtil.getInstance(FileController.class, FileController::new);

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        // 从JobDataMap中获取参数
        String jobId = jobDataMap.getString("jobId");

        int similarityThreshold = Integer.parseInt(InitConfigUtil.getConfig("defaultSimilarityThreshold"));

        File file1 = fileController.getFiles(jobId);
        List<SimHash> simHashesDoc1 = fileController.getSimHashesDoc1(jobId);
        Map<File, List<SimHash>> simHashesDoc2 = fileController.getSimHashesDocs2();

        simHashesDoc2.forEach((file2, simHashesDoc) -> {
            if (!file1.getName().equals(file2.getName())) {
                FileData fileData = new FileData();
                BigDecimal similarSentencesRatio = FileCompareUtil.compareFiles(simHashesDoc1, simHashesDoc);
                if (similarSentencesRatio.compareTo(new BigDecimal(similarityThreshold)) > 0) {
                    fileData.setFileName1(FileUtil.getName(file1));
                    fileData.setFilePath1(file1.getPath());
                    fileData.setFileName2(FileUtil.getName(file2));
                    fileData.setFilePath2(file2.getPath());
                    fileData.setPercent(similarSentencesRatio + "%");
                    fileController.setFileDatas(fileData);
                }
            }
        });
    }
}

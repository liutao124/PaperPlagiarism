package cn.octopus.paperplagiarism.controller;

import cn.hutool.core.util.IdUtil;
import cn.octopus.paperplagiarism.listener.JobTimeListener;
import cn.octopus.paperplagiarism.service.FileService;
import cn.octopus.paperplagiarism.util.PaginationUtil;
import cn.octopus.paperplagiarism.util.SimHash;
import cn.octopus.paperplagiarism.vo.FileData;
import cn.octopus.paperplagiarism.vo.InitConfigUtil;
import cn.octopus.paperplagiarism.vo.Page;
import lombok.Getter;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FileController {

    private static Scheduler scheduler;
    @Getter
    private Map<File, List<SimHash>> simHashesDocs1 = new HashMap<>();
    @Getter
    private Map<File, List<SimHash>> simHashesDocs2 = new HashMap<>();
    private Map<String, File> files = new HashMap<>();
    private Map<String, List<SimHash>> simHashesDoc1 = new HashMap<>();
    private Map<String, String> jobStatus = new HashMap<>();
    private static List<FileData> fileDatas  = new ArrayList<>();

    public void startQuartz(){
        try {
            if(null == scheduler){
                String fileConcurrent = InitConfigUtil.getConfig("fileConcurrent");
                // 配置Quartz属性
                Properties props = new Properties();
                props.put("org.quartz.scheduler.instanceName", "fileScheduler");
                props.put("org.quartz.threadPool.threadCount", fileConcurrent);
                props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore" );

                // 初始化Scheduler
                SchedulerFactory schedulerFactory = new StdSchedulerFactory(props);
                scheduler = schedulerFactory.getScheduler();

                JobTimeListener jobListener = new JobTimeListener();
                scheduler.getListenerManager().addJobListener(jobListener);

                scheduler.start();
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void simHashesDocs(String folderPath1, String folderPath2) {
        simHashesDocs1 = SimHash.getSimHashesDoc(folderPath1);
        simHashesDocs2 = SimHash.getSimHashesDoc(folderPath2);
    }

    public void getFileData(File file1, List<SimHash> simHashesDoc) {
        try {
            String jobId = IdUtil.simpleUUID();
            jobStatus.put(jobId, "start");
            files.put(jobId, file1);
            simHashesDoc1.put(jobId, simHashesDoc);

            // 定义JobDetail和Trigger
            JobDetail job = JobBuilder.newJob(FileService.class)
                    .withIdentity("fileJob-" + jobId, "fileGroup")
                    .usingJobData("jobId", jobId)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("fileTrigger" + jobId, "fileGroup")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                    .startNow()
                    .build();

            // 调度作业
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    // 检查所有任务是否完成
    public void checkJobCompleted() {
        boolean completed = true;
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            for (Map.Entry<String, String> entry : jobStatus.entrySet()) {
                if ("start".equals(entry.getValue())) {
                    completed = false;
                    break;
                }else{
                    completed = true;
                }
            }
            if (completed) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public File getFiles(String jobId) {
        return files.get(jobId);
    }

    public List<SimHash> getSimHashesDoc1(String jobId) {
        return simHashesDoc1.get(jobId);
    }

    public void markJobCompleted(String jobId) {
        jobStatus.put(jobId, "COMPLETED");
    }

    public void setFileDatas(FileData fileData) {
        fileDatas.add(fileData);
    }

    public void clearFileData() {
        fileDatas.clear();
    }

    public void endClearAll() {
        simHashesDocs1.clear();
        simHashesDocs2.clear();
        files.clear();
        simHashesDoc1.clear();
        jobStatus.clear();
    }

    public Page getFileDataByPages(int curPage, int pageSize) {
        if (null == fileDatas) {
            return new Page();
        }
        List<FileData> subList = PaginationUtil.getPageData(fileDatas, curPage, pageSize);
        Page page = new Page();
        page.setCurPage(curPage);
        page.setPages(PaginationUtil.calculateTotalPages(fileDatas.size(), pageSize));
        page.setTotal(fileDatas.size());
        page.setResultData(subList);
        return page;
    }

    public List<FileData> getFileDatas() {
        if (null == fileDatas)  {
            return new ArrayList<>();
        }else{
            return fileDatas;
        }
    }

}

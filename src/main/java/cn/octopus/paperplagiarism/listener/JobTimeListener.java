package cn.octopus.paperplagiarism.listener;

import cn.hutool.core.date.DateUtil;
import cn.octopus.paperplagiarism.controller.FileController;
import cn.octopus.paperplagiarism.util.ObjectInstUtil;
import org.quartz.*;

public class JobTimeListener implements JobListener {

    private static final FileController fileController = ObjectInstUtil.getInstance(FileController.class, FileController::new);

    @Override
    public String getName() {
        return "JobTimeListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // 任务即将执行，记录开始时间
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        long startTime = System.currentTimeMillis();
        jobDataMap.put("startTime", startTime);
        System.out.println("[" + jobDetail.getKey() + "] 开始执行时间: " + DateUtil.now());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        // 任务执行完成，计算并记录执行时间
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        long startTime = jobDataMap.getLong("startTime");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("[" + jobDetail.getKey() + "] 执行完成时间: " + DateUtil.now());
        System.out.println("[" + jobDetail.getKey() + "] 执行耗时: " + executionTime + " 毫秒");

        // 清理数据（可选）
        fileController.markJobCompleted(jobDataMap.getString("jobId"));
    }

    // jobExecutionVetoed 方法通常用于处理任务被拒绝的情况，此处省略
}
package com.sen.scheduler.v3;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 可以并发执行的JOB(默认)
 */
public class TestJob implements Job {
    private static final Logger logger = LogManager.getLogger(TestJob.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("***************TestJob execute start!");
        String v1 = context.getMergedJobDataMap().getString("testKey");  //使用配置文件中的参数
        String v2 = context.getMergedJobDataMap().getString("key");
        System.out.println("***************:"+v1+"/"+v2);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("TestJob execute end!***************");
    }
}

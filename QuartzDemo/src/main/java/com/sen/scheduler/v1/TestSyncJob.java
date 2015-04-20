package com.sen.scheduler.v1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

/**
 * 添加注解：@DisallowConcurrentExecution
 * 同步执行的Job,不允许并发执行
 * (上一任务没有执行完毕，下一任务将阻塞等待)
 */
@DisallowConcurrentExecution
public class TestSyncJob implements Job {
    private static final Logger logger = LogManager.getLogger(TestSyncJob.class);
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("***************TestSyncJob execute start!");
        try {
          //获取ApplicationContext,方式1
            //ApplicationContext appCxt = SpringConext.getApplicationContext();
          //获取ApplicationContext,方式2
            ApplicationContext appCxt = (ApplicationContext)context.getScheduler().getContext().get("applicationContext"); 
        } catch(SchedulerException e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("TestSyncJob execute end!***************");
    }
}

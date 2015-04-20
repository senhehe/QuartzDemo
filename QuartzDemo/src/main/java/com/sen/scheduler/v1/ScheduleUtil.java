package com.sen.scheduler.v1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.InitializingBean;

public class ScheduleUtil implements InitializingBean {
    
    private static final Logger logger = LogManager.getLogger(ScheduleUtil.class);
    private static Scheduler scheduler = null;

    /**
     * 获取jobKey
     * @param jobName
     * @param jobGroup
     * @return
     */
    public static JobKey getJobKey(String jobName, String jobGroup) {
        return JobKey.jobKey(jobName, jobGroup);
    }
    
    /**
     * 创建定时任务
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean createJob(ScheduleJob sj) {
        try {
            if (scheduler.checkExists(sj.triggerKey())) {
                logger.error("***************创建定时任务失败：已经存在Trigger【"+sj.triggerKey()+"】***************");
                return false;
            }
            scheduler.scheduleJob(sj.jobDetail(), sj.cronTrigger(null));
            return true;
        } catch (SchedulerException e) {
            logger.error("***************创建定时任务："+sj.getJobGroup()+"-"+sj.getJobName()+"失败***************", e);
            return false;
        }
    }

    /**
     * 更新定时任务时间
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean updateJob(ScheduleJob sj) {
        try {
            if (scheduler.checkExists(sj.triggerKey())) {
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(sj.triggerKey());
                scheduler.rescheduleJob(sj.triggerKey(), sj.cronTrigger(trigger));
                return true;
            }
            logger.error("***************更新定时任务失败：不存在Trigger【"+sj.triggerKey()+"】***************");
            return false;
        } catch (SchedulerException e) {
            logger.error("***************更新定时任务："+sj.getJobGroup()+"-"+sj.getJobName()+"失败***************", e);
            return false;
        }
    }
    
    /**
     * 运行一次任务
     * @param jobName
     * @param jobGroup
     * @return boolean
     */
    public static boolean runOnce(String jobName, String jobGroup) {
        try {
            scheduler.triggerJob(getJobKey(jobName, jobGroup));
            return true;
        } catch (SchedulerException e) {
            logger.error("***************运行一次定时任务："+jobGroup+"-"+jobName+"失败***************", e);
            return false;
        }
    }

    /**
     * 暂停任务
     * @param jobName
     * @param jobGroup
     * @return boolean
     */
    public static boolean pauseJob(String jobName, String jobGroup) {
        try {
            scheduler.pauseJob(getJobKey(jobName, jobGroup));
            return true;
        } catch (SchedulerException e) {
            logger.error("***************暂停定时任务："+jobGroup+"-"+jobName+"失败***************", e);
            return false;
        }
    }

    /**
     * 恢复任务
     * @param jobName
     * @param jobGroup
     * @return boolean
     */
    public static boolean resumeJob(String jobName, String jobGroup) {
        try {
            scheduler.resumeJob(getJobKey(jobName, jobGroup));
            return true;
        } catch (SchedulerException e) {
            logger.error("***************恢复定时任务："+jobGroup+"-"+jobName+"失败***************", e);
            return false;
        }
    }

    /**
     * 删除定时任务
     * @param jobName
     * @param jobGroup
     * @return boolean
     */
    public static boolean deleteJob(String jobName, String jobGroup) {
        try {
            return scheduler.deleteJob(getJobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            logger.error("***************删除定时任务："+jobGroup+"-"+jobName+"失败***************", e);
            return false;
        }
    }
    
    public void setScheduler(Scheduler scheduler) {
        ScheduleUtil.scheduler = scheduler;
    }

    public void afterPropertiesSet() throws Exception {
        logger.info("***************Initial ScheduleUtil successful***************");
    }

}

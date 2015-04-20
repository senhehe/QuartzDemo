package com.sen.scheduler.v3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.InitializingBean;

public class ScheduleUtil implements InitializingBean {
    
    private static final Logger logger = LogManager.getLogger(ScheduleUtil.class);
    private static Scheduler scheduler = null;

    /**
     * 校验Corn表达式
     * @param corn
     * @return boolean
     */
    public static boolean validateCornExpression(String corn) {
        return CronExpression.isValidExpression(corn);
    }
    
    /**
     * 创建定时任务(单触发器)
     * @param ScheduleJob
     * @param ScheduleTrigger
     * @return boolean
     */
    public static boolean createJob(ScheduleJob sj,ScheduleTrigger st) {
        try {
            if (checkTriggerExists(st)) {
                logger.error("createJob创建定时任务失败：已经存在Trigger【"+st.triggerKey()+"】");
                return false;
            }
            scheduler.scheduleJob(sj.jobDetail(), st.trigger(null));
            return true;
        } catch (SchedulerException e) {
            logger.error("createJob创建定时任务："+sj.jobKey().toString()+"/"+st.triggerKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 创建定时任务(多触发器)
     * @param ScheduleJob
     * @param List<ScheduleTrigger>
     * @return boolean
     */
    public static boolean createJobWithTriggers(ScheduleJob sj,List<ScheduleTrigger> stl,boolean ifReplace) {
        try {
            if (stl!=null && stl.size()>0) {
                Set<Trigger> sts = new HashSet<Trigger>();
                for(int i=0; i<stl.size(); i++) {
                    ScheduleTrigger st = stl.get(i);
                    sts.add(st.trigger(null));
                }
                scheduler.scheduleJob(sj.jobDetail(), sts, ifReplace);
            }
            return true;
        } catch (SchedulerException e) {
            logger.error("createJobWithTriggers创建定时任务："+sj.jobKey().toString()+"失败", e);
            return false;
        }
    }

    /**
     * 更新已存在(group和name相同)Trigger的时间
     * @param ScheduleTrigger
     * @return boolean
     */
    public static boolean updateTrigger(ScheduleTrigger st) {
        try {
            if (checkTriggerExists(st)) {
                Trigger trigger = scheduler.getTrigger(st.triggerKey());
                scheduler.rescheduleJob(st.triggerKey(), st.trigger(trigger));
                return true;
            }
            logger.error("updateJobByTrigger更新定时任务时间失败：不存在Trigger【"+st.triggerKey()+"】");
            return false;
        } catch (SchedulerException e) {
            logger.error("updateJobByTrigger更新定时任务时间："+st.triggerKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 检查Job是否已存在
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean checkJobExists(ScheduleJob sj) {
        try {
            return scheduler.checkExists(sj.jobKey());
        } catch (SchedulerException e) {
            return false;
        }
    }
    
    /**
     * 检查Trigger是否已存在
     * @param ScheduleTrigger
     * @return boolean
     */
    public static boolean checkTriggerExists(ScheduleTrigger st) {
        try {
            return scheduler.checkExists(st.triggerKey());
        } catch (SchedulerException e) {
            return false;
        }
    }
    
    /**
     * 向容器中添加Job任务
     * 注：只有手动run或关联到Trigger才能触发
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean addJob(ScheduleJob sj,boolean ifReplace) {
        try {
            scheduler.addJob(sj.jobDetail(), ifReplace);
            return true;
        } catch (SchedulerException e) {
            logger.error("addJob添加任务："+sj.jobKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 通过Trigger创建任务
     * 注：必须确保Trigger已经和容器中某个Job关联(即trigger中设定了job)
     * @param ScheduleTrigger
     * @return boolean
     */
    public static boolean createJobByTrigger(ScheduleTrigger st) {
        try {
            if (checkTriggerExists(st)) {
                logger.error("createJobByTrigger创建定时任务失败：已经存在Trigger【"+st.triggerKey()+"】");
                return false;
            }
            scheduler.scheduleJob(st.trigger(null));
            return true;
        } catch (SchedulerException e) {
            logger.error("createJobByTrigger添加任务："+st.triggerKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 运行一次任务
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean runOnce(ScheduleJob sj) {
        try {
            scheduler.triggerJob(sj.jobKey());
            return true;
        } catch (SchedulerException e) {
            logger.error("运行一次定时任务："+sj.jobKey().toString()+"失败", e);
            return false;
        }
    }

    /**
     * 暂停任务(对应的trigger都将暂停)
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean pauseJob(ScheduleJob sj) {
        try {
            scheduler.pauseJob(sj.jobKey());
            return true;
        } catch (SchedulerException e) {
            logger.error("pauseJob暂停定时任务："+sj.jobKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 根据Trigger暂停任务
     * @param ScheduleTrigger
     * @return boolean
     */
    public static boolean pauseJobByTrigger(ScheduleTrigger st) {
        try {
            scheduler.pauseTrigger(st.triggerKey());
            return true;
        } catch (SchedulerException e) {
            logger.error("pauseJobByTrigger暂停定时任务："+st.triggerKey().toString()+"失败", e);
            return false;
        }
    }

    /**
     * 恢复任务
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean resumeJob(ScheduleJob sj) {
        try {
            scheduler.resumeJob(sj.jobKey());
            return true;
        } catch (SchedulerException e) {
            logger.error("resumeJob恢复定时任务："+sj.jobKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 根据Trigger恢复任务
     * @param ScheduleTrigger
     * @return boolean
     */
    public static boolean resumeJobByTrigger(ScheduleTrigger st) {
        try {
            scheduler.resumeTrigger(st.triggerKey());
            return true;
        } catch (SchedulerException e) {
            logger.error("resumeJobByTrigger恢复定时任务："+st.triggerKey().toString()+"失败", e);
            return false;
        }
    }

    /**
     * 删除定时任务(对应的trigger都将删除)
     * @param ScheduleJob
     * @return boolean
     */
    public static boolean deleteJob(ScheduleJob sj) {
        try {
            return scheduler.deleteJob(sj.jobKey());
        } catch (SchedulerException e) {
            logger.error("deleteJob删除定时任务："+sj.jobKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 根据Trigger删除定时任务
     * @param ScheduleTrigger
     * @return boolean
     */
    public static boolean deleteJobByTrigger(ScheduleTrigger st) {
        try {
            return scheduler.unscheduleJob(st.triggerKey());
        } catch (SchedulerException e) {
            logger.error("deleteJobByTrigger删除定时任务："+st.triggerKey().toString()+"失败", e);
            return false;
        }
    }
    
    /**
     * 清除所有定时任务(所有Job和Trigger)
     * @return boolean
     */
    public static boolean clearSchedule() {
        try {
            scheduler.clear();
            return true;
        } catch(SchedulerException e) {
            logger.error("clearSchedule清理定时任务失败", e);
            return false;
        }
    }
    
    public void setScheduler(Scheduler scheduler) {
        ScheduleUtil.scheduler = scheduler;
    }

    public void afterPropertiesSet() throws Exception {
        logger.info("Initial ScheduleUtil successful");
    }

}

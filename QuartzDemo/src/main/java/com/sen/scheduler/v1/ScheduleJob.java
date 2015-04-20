package com.sen.scheduler.v1;

import java.util.Date;
import java.util.Map;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import com.sen.common.DateUtil;

public class ScheduleJob {
    
    /* job所在分组   */
    private String jobGroup = Scheduler.DEFAULT_GROUP;
    /* job名称   */
    private String jobName;
    /* 目标执行类  */
    private Class<? extends Job> target;
    /* corn表达式   */
    private String cornExpression;
    /* 开始时间   */
    private String startTime;
    /* 结束时间   */
    private String endTime;
    
    /* 其他-参数-属性   */
    private TriggerKey triggerKey;
    private JobDetail jobDetail;
    
    public ScheduleJob() {}
    
    public ScheduleJob(String jobName) {
        this.jobName = jobName;
    }
    
    public ScheduleJob(String jobGroup,String jobName) {
        this.jobGroup = jobGroup;
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Class<? extends Job> getTarget() {
        return target;
    }

    public ScheduleJob setTarget(Class<? extends Job> target) {
        this.target = target;
        return this;
    }

    public String getCornExpression() {
        return cornExpression;
    }

    public ScheduleJob setCornExpression(String cornExpression) {
        this.cornExpression = cornExpression;
        return this;
    }

    public Date getStartTime() {
        return DateUtil.toDatetime(startTime);
    }

    public ScheduleJob setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return DateUtil.toDatetime(endTime);
    }

    public ScheduleJob setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }
    
    public static boolean validateCornExpression(String corn) {
        return CronExpression.isValidExpression(corn);
    }
    
    public TriggerKey triggerKey() {
        if (triggerKey == null) {
            triggerKey = TriggerKey.triggerKey(this.jobName, this.jobGroup);
        }
        return triggerKey;
    }
    
    public CronTrigger cronTrigger(CronTrigger trigger) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(getCornExpression());
        TriggerBuilder<CronTrigger> tb = null;
        if(trigger == null) {
            tb = TriggerBuilder.newTrigger().withIdentity(triggerKey())
                    .withSchedule(cronScheduleBuilder);
        }else {
            tb =  trigger.getTriggerBuilder().withIdentity(triggerKey())
                    .withSchedule(cronScheduleBuilder);
        }
        if(startTime != null ) {
            tb.startAt(getStartTime());
        }
        if(endTime != null) {
            tb.endAt(getEndTime());
        }
        return tb.build();
    }

    public JobDetail jobDetail() {
        if (jobDetail == null) {
            jobDetail = JobBuilder.newJob(target)
                    .withIdentity(this.jobName, this.jobGroup)
                    .build();
        }
        return jobDetail;
    }

    /**
     * 传参数给执行的job(key,value)
     * (在job中 通过 context.getMergedJobDataMap().get(key) 获取值)
     */
    public ScheduleJob addJobData(String key, Object value) {
        final JobDetail detail = jobDetail();
        final JobDataMap jobDataMap = detail.getJobDataMap();
        jobDataMap.put(key, value);
        return this;
    }

    /**
     * 传参数给执行的job(Map)
     * (在job中 通过 context.getMergedJobDataMap().get(key) 获取值)
     */
    public ScheduleJob addJobDataMap(Map<String, Object> map) {
        final JobDetail detail = jobDetail();
        final JobDataMap jobDataMap = detail.getJobDataMap();
        jobDataMap.putAll(map);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{target=").append(target);
        sb.append(", jobGroup='").append(jobGroup).append('\'');
        sb.append(", jobName='").append(jobName).append('\'');
        sb.append(", cornExpression='").append(cornExpression).append('\'');
        sb.append(", startTime='").append(startTime).append('\'');
        sb.append(", endTime='").append(endTime);
        sb.append('}');
        return sb.toString();
    }
    
}
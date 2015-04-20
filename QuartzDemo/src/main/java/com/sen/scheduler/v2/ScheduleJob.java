package com.sen.scheduler.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;

public class ScheduleJob {
    
    /* job所在分组   */
    private String jobGroup;
    /* job名称   */
    private String jobName;
    /* job描述   */
    private String jobDesc;
    /* 目标执行类  */
    private Class<? extends Job> target;
    /* 触发器   */
    private List<ScheduleTrigger> triggers;
    
    /* 其他-参数-属性   */
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
    
    public String getJobDesc() {
        return jobDesc;
    }

    public ScheduleJob setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
        return this;
    }
    
    public List<ScheduleTrigger> getTriggers() {
        return triggers;
    }

    public ScheduleJob setTriggers(List<ScheduleTrigger> triggers) {
        this.triggers = triggers;
        return this;
    }
    
    public void addTrigger(ScheduleTrigger trigger) {
        if(this.triggers == null) {
            this.triggers = new ArrayList<ScheduleTrigger>();
        }
        if(trigger.getSj() == null) {
            trigger.setSj(this);
        }
        this.triggers.add(trigger);
    }
    
    public JobKey jobKey() {
        return JobKey.jobKey(this.jobName, this.jobGroup);
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
        sb.append("{jobGroup:'").append(jobGroup).append("'");
        sb.append(", jobName:'").append(jobName).append("'");
        sb.append(", jobDesc:'").append(jobDesc).append("'");
        sb.append(", target:'").append(target).append("'");
        sb.append(", triggers:").append(triggers.toString()).append("}");
        return sb.toString();
    }
    
}
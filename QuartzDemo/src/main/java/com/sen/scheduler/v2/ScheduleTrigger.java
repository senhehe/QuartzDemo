package com.sen.scheduler.v2;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import com.sen.common.DateUtil;

public class ScheduleTrigger {
    
    /* trigger分组   */
    private String triggerGroup;
    /* trigger名称   */
    private String triggerName;
    /* trigger描述   */
    private String triggerDesc;
    /* 优先级  */
    private String priority;
    /* 定时类型  */
    private String type;
    /* 定时表达式   */
    private String expression;
    /* 开始时间   */
    private String startAt;
    /* 结束时间   */
    private String endAt;
    /* 任务   */
    private ScheduleJob sj;
    
    public ScheduleTrigger() {}
    
    public ScheduleTrigger(String triggerName) {
        this.triggerName = triggerName;
    }
    
    public ScheduleTrigger(String triggerGroup,String triggerName) {
        this.triggerGroup = triggerGroup;
        this.triggerName = triggerName;
    }
    
    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerDesc() {
        return triggerDesc;
    }

    public ScheduleTrigger setTriggerDesc(String triggerDesc) {
        this.triggerDesc = triggerDesc;
        return this;
    }

    public String getPriority() {
        return priority;
    }

    public ScheduleTrigger setPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public String getType() {
        return type;
    }

    public ScheduleTrigger setType(String type) {
        this.type = type;
        return this;
    }

    public String getExpression() {
        return expression;
    }

    public ScheduleTrigger setExpression(String expression) {
        this.expression = expression;
        return this;
    }

    public Date getStartAt() {
        return DateUtil.toDatetime(startAt);
    }

    public ScheduleTrigger setStartAt(String startAt) {
        this.startAt = startAt;
        return this;
    }

    public Date getEndAt() {
        return DateUtil.toDatetime(endAt);
    }

    public ScheduleTrigger setEndAt(String endAt) {
        this.endAt = endAt;
        return this;
    }
    
    public ScheduleJob getSj() {
        return sj;
    }

    public ScheduleTrigger setSj(ScheduleJob sj) {
        this.sj = sj;
        if(this.triggerGroup == null) {
            this.triggerGroup = sj.getJobGroup();
        }
        return this;
    }
    
    public TriggerKey triggerKey() {
        return TriggerKey.triggerKey(this.triggerName, this.triggerGroup);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Trigger trigger(Trigger oldt) {
        TriggerBuilder tb = null;
        if(oldt==null) {
            tb = TriggerBuilder.newTrigger().withIdentity(triggerKey()).withSchedule(buildSchedule());
        }else {
            tb =  oldt.getTriggerBuilder().withIdentity(triggerKey()).withSchedule(buildSchedule());
        }
        if(this.sj != null) {
            tb.forJob(sj.jobKey());
        }
        if(StringUtils.isNotBlank(this.priority)) {
            tb.withPriority(Integer.parseInt(priority));
        }
        if(StringUtils.isNotBlank(this.startAt)) {
            tb.startAt(getStartAt());
        }
        if(StringUtils.isNotBlank(this.endAt)) {
            tb.endAt(getStartAt());
        }
        return tb.build();
    }
    
    @SuppressWarnings("rawtypes")
    private ScheduleBuilder buildSchedule() {
        ScheduleBuilder sb = null;
        if(this.type.equals("corn")) {
            if(!CronExpression.isValidExpression(this.expression)) {
                throw new ScheduleException("ScheduleTrigger***配置信息***定时Corn表达式格式错误!!!");
            }
            sb = CronScheduleBuilder.cronSchedule(this.expression);
        }else {
            String subType = this.type.split("-")[1];
            String[] values = this.expression.trim().split("/");
            SimpleScheduleBuilder ssb = null;
            switch(subType) {
            case "S":
                ssb = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(Integer.parseInt(values[0]));
                break;
            case "MI":
                ssb = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(Integer.parseInt(values[0]));
                break;
            case "H":
                ssb = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(Integer.parseInt(values[0]));
                break;
            case "D":
                sb = CronScheduleBuilder.dailyAtHourAndMinute(
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]));
                break;
            case "W":
                sb = CronScheduleBuilder.weeklyOnDayAndHourAndMinute(
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]));
                break;
            case "M":
                sb = CronScheduleBuilder.monthlyOnDayAndHourAndMinute(
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]));
                break;
            }
            if(ssb != null) {
                if(values[1].equalsIgnoreCase("N")) {
                    ssb.repeatForever();
                }else {
                    ssb.withRepeatCount(Integer.parseInt(values[1]));
                }
                sb = ssb;
            }
        }
        return sb;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{triggerName:'").append(triggerName).append("'");
        sb.append(", priority:'").append(priority).append("'");
        sb.append(", type:'").append(type).append("'");
        sb.append(", expression:'").append(expression).append("'");
        sb.append(", startAt:'").append(startAt).append("'");
        sb.append(", endAt:'").append(endAt).append("'");
        sb.append(", triggerDesc:'").append(triggerDesc).append("'}");
        return sb.toString();
    }
    
}
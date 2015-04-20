package com.sen.scheduler.v2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.Job;

public class LoadJobUtil {
    private static final Logger logger = LogManager.getLogger(LoadJobUtil.class);
    private String jobPropsFileName = "schedule.xml";
    private Document document = null;
    private List<ScheduleJob> sjl = null;
    
    /**
     * 初始化方法
     */
    public void initialize() {
        logger.info("Begin To Load Schedule Jobs ");
        loadJobProps();
        Element enabled = (Element) document.selectSingleNode("//ScheduleJobs/enableSchedule");
        if(enabled.getText().equals("true")) {
            loadScheduleJobs();
            schedule();
        }
        logger.info("End To Load Schedule Jobs ");
    }
    
    /**
     * 添加任务定时
     */
    private void schedule() {
        if(sjl != null && sjl.size()>0) {
            for(int i = 0; i < sjl.size(); i++) {
                ScheduleJob sj = sjl.get(i);
                ScheduleUtil.createJobWithTriggers(sj, sj.getTriggers(), true);
            }
        }
    }
    
    /**
     * 加载所有定时任务
     */
    @SuppressWarnings("unchecked")
    private void loadScheduleJobs() {
        sjl = new ArrayList<ScheduleJob>();
        try{
            List<Element> groups = document.selectNodes("//ScheduleJobs/jobs/group");
            for (Iterator<Element> groupIterator = groups.iterator(); groupIterator.hasNext();) {
                Element group = (Element) groupIterator.next();
                String groupName = group.attribute("name").getText();
                List<Element> jobs = group.elements("job");
                for (Iterator<Element> jobIterator = jobs.iterator(); jobIterator.hasNext();) {
                    Element job = (Element) jobIterator.next();
                    ScheduleJob sj = getJob(groupName,job);
                    if(sj!=null) {
                        sjl.add(sj);
                    }
                }
            }
        }catch(Exception e) {
            logger.error("初始化定时任务/加载Jobs/失败", e);
        }
    }
    
    /**
     * 获取job配置信息
     */
    @SuppressWarnings("unchecked")
    private ScheduleJob getJob(String jobGroup,Element job) throws Exception {
        ScheduleJob sj = null;
        String jobName = job.attributeValue("name");
        String jobClass = job.attributeValue("class");
        String jobDesc = job.attributeValue("desc");
        Element dataMapElement = job.element("jobDataMap");
        Map<String,Object> jobDataMap = null;
        if(dataMapElement!=null) {
            jobDataMap = new HashMap<String,Object>();
            List<Element> datas = dataMapElement.elements("item");
            for (Iterator<Element> dataIterator = datas.iterator(); dataIterator.hasNext();) {
                Element data = (Element) dataIterator.next();
                jobDataMap.put(data.attributeValue("key"), data.attributeValue("value"));
            }
        }
        
        if(StringUtils.isNotBlank(jobGroup) && StringUtils.isNotBlank(jobName) && StringUtils.isNotBlank(jobClass)) {
            sj = new ScheduleJob(jobGroup, jobName).setTarget(
                    (Class<? extends Job>) Class.forName(jobClass)).setJobDesc(jobDesc);
            if(jobDataMap!=null && jobDataMap.size()>0) {
                sj.addJobDataMap(jobDataMap);
            }
            List<Element> triggers = job.element("triggers").elements("trigger");
            for (Iterator<Element> triggerIterator = triggers.iterator(); triggerIterator.hasNext();) {
                Element trigger = (Element) triggerIterator.next();
                sj.addTrigger(new ScheduleTrigger(trigger.attributeValue("name")).setPriority(
                        trigger.attributeValue("priority")).setType(trigger.attributeValue("type")).setExpression(
                        trigger.attributeValue("expression")).setStartAt(trigger.attributeValue("startAt")).setEndAt(
                        trigger.attributeValue("endAt")).setTriggerDesc(trigger.attributeValue("desc")));
            }
            logger.info(sj.toString());
        }else {
            logger.info("初始化定时任务/"+jobGroup+"-"+jobName+"/配置信息不正确");
        }
        return sj;
    }
    
    /**
     * 加载配置文件
     */
    private void loadJobProps() {
        if(document != null) {
            return;
        }
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(jobPropsFileName);
            document = new SAXReader().read(inputStream);
            inputStream.close();
        } catch(Exception e) {
            logger.error("初始化定时任务***读取文件***失败", e);
        }
    }

    public void setJobPropsFileName(String jobPropsFileName) {
        this.jobPropsFileName = jobPropsFileName;
    }

}

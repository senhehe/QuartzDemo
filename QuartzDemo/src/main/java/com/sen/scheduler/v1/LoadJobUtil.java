package com.sen.scheduler.v1;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;

public class LoadJobUtil {
    private static final Logger logger = LogManager.getLogger(LoadJobUtil.class);
    private static final String ENABLE_SCHEDULE = "EnableSchedule";
    private static final String SCHEDULE_LIST = "ScheduleList";
    private static final String JOB_GROUP = "jobGroup";
    private static final String JOB_NAME = "jobName";
    private static final String JOB_CLASS = "jobClass";
    private static final String JOB_DATAMAP = "jobDataMap";
    private static final String CORN_EXPRESSION = "cornExpression";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String SL_DELIMITER = ",";
    private static final String DM_DELIMITER = ";";
    private static String jobPropsFileName = "/schedule.properties";
    private Properties jobProps = null;
    private List<ScheduleJob> sjl = null;
    
    /**
     * 初始化方法
     */
    public void initialize() {
        logger.info("***************Begin To Load Schedule Jobs ***************");
        loadJobProps();
        if(StringUtils.isNotBlank(jobProps.getProperty(ENABLE_SCHEDULE)) && jobProps.getProperty(ENABLE_SCHEDULE).equals("true")) {
            loadScheduleJobs(SCHEDULE_LIST,SL_DELIMITER,jobProps);
            schedule();
        }
        logger.info("***************End To Load Schedule Jobs***************");
    }
    
    /**
     * 添加任务定时
     */
    private void schedule() {
        if(sjl != null && sjl.size()>0) {
            for(int i = 0; i < sjl.size(); i++) {
                ScheduleUtil.createJob(sjl.get(i));
            }
        }
    }
    
    /**
     * 加载所有定时任务
     */
    private void loadScheduleJobs(String slist,String delimiter, Properties props) {
        StringTokenizer stj = new StringTokenizer(props.getProperty(slist), delimiter);
        String[] jobLists = new String[stj.countTokens()];
        int count = 0;
        while (stj.hasMoreTokens()) {
            String token = stj.nextToken().toString().trim();
            jobLists[count] = token;
            count++;
        }

        sjl = new ArrayList<ScheduleJob>();
        try{
            for (int i = 0; i < jobLists.length; i++) {
                ScheduleJob sj = getJobs(props, jobLists[i]);
                if(sj != null) {
                    sjl.add(sj);
                }
            }
        }catch(Exception e) {
            logger.error("***************初始化定时任务/加载Job/失败***************", e);
        }
    }
    
    /**
     * 解析properties获取job配置信息
     */
    private ScheduleJob getJobs(Properties props,String job) throws Exception{
        ScheduleJob sj = null;
        String jobGroup = props.getProperty(job + "."+ JOB_GROUP);
        String jobName = props.getProperty(job + "."+ JOB_NAME);
        String jobClass = props.getProperty(job + "."+ JOB_CLASS);
        String jobDataMapStr = props.getProperty(job + "."+ JOB_DATAMAP);
        Map<String,Object> jobDataMap = null;
        if(StringUtils.isNotBlank(jobDataMapStr)) {
            jobDataMap = new HashMap<String,Object>();
            StringTokenizer stj = new StringTokenizer(jobDataMapStr, DM_DELIMITER);
            while (stj.hasMoreTokens()) {
                String token = stj.nextToken().toString().trim();
                String[] map = token.split("/");
                jobDataMap.put(map[0], map[1]);
            }
        }
        String cornExpression = props.getProperty(job + "."+ CORN_EXPRESSION);
        String startTime = props.getProperty(job + "."+ START_TIME);
        String endTime = props.getProperty(job + "."+ END_TIME);
        if(StringUtils.isNotBlank(jobGroup) && StringUtils.isNotBlank(jobName) && StringUtils.isNotBlank(jobClass) && StringUtils.isNotBlank(cornExpression)) {
            if(ScheduleJob.validateCornExpression(cornExpression)) {
                sj = new ScheduleJob(jobGroup, jobName).setTarget(
                        (Class<? extends Job>) Class.forName(jobClass))
                        .setCornExpression(cornExpression);
                if(jobDataMap!=null && jobDataMap.size()>0) {
                    sj.addJobDataMap(jobDataMap);
                }
                if(StringUtils.isNotBlank(startTime)) {
                    sj.setStartTime(startTime);
                }
                if(StringUtils.isNotBlank(endTime)) {
                    sj.setEndTime(endTime);
                }
                logger.info(sj.toString());
            }else {
                logger.info("***************初始化定时任务/Corn表达式"+cornExpression+"不正确***************");
            }
        }else {
            logger.info("***************初始化定时任务/"+job+"/配置信息不正确***************");
        }
        return sj;
    }
    
    /**
     * 加载properties
     */
    private Properties loadJobProps() {
        if(jobProps != null) {
            return jobProps;
        }
        jobProps = new Properties();
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(jobPropsFileName);
            jobProps.load(inputStream);
            inputStream.close();
        } catch(IOException e) {
            logger.error("***************初始化定时任务/读取文件/失败***************", e);
        }
        return jobProps;
    }

    public static void setJobPropsFileName(String jobPropsFileName) {
        LoadJobUtil.jobPropsFileName = jobPropsFileName;
    }

}

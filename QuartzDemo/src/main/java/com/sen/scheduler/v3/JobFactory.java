package com.sen.scheduler.v3;

import java.lang.reflect.Method;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.sen.common.SpringContext;

public class JobFactory implements Job {
    private static final Logger logger = LogManager.getLogger(JobSyncFactory.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String factoryType = dataMap.getString("Job_Param_factoryType");
        String target = dataMap.getString("Job_Param_target");
        String method = dataMap.getString("Job_Param_method");
        Class<?> clazz = null;
        Object object = null;
        try {
            if(factoryType.equals("new")) {
                clazz = Class.forName(target);
                object = clazz.newInstance();
            }else {
                if(SpringContext.containsBean(target)) {
                    object = SpringContext.getBean(target);
                }else {
                    object = SpringContext.getBean(Class.forName(target));
                }
                if(object == null) {
                    throw new ScheduleException("JobFactory从Spring容器中无法获取目标执行类!");
                }
                clazz = object.getClass();
            }
            Method m = clazz.getDeclaredMethod(method);
            m.invoke(object);
        } catch(Exception e) {
            logger.error("JobFactory执行错误!",e);
        }
    }
}

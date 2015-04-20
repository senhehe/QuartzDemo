package com.sen.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext context;

	public void setApplicationContext(ApplicationContext acx) {
		context = acx;
	}
	
	public static ApplicationContext getApplicationContext() {
		return context;
	}
	
	public static <T> T getBean(Class<T> clazz) {
        if (context == null) {
            return null;
        }
        return context.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanId) {
        if (context == null) {
            return null;
        }
        return (T) context.getBean(beanId);
    }
    
    public static boolean containsBean(String beanId) {
        if (context == null) {
            return false;
        }
        return context.containsBean(beanId);
    }

}

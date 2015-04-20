package com.sen.scheduler.v1;


public class ScheduleException extends RuntimeException {
    
    private static final long serialVersionUID = -6728890159419756012L;

    public ScheduleException() {
        super();
    }

    public ScheduleException(Throwable e) {
        super(e);
    }

    public ScheduleException(String message) {
        super(message);
    }
    
}

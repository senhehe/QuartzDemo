package com.sen.scheduler.v3;

import org.springframework.stereotype.Service;

@Service
public class TestSpring {

    public void test() {
        System.out.println("********TestSpring Schedule Target Method Execute!!!********");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}

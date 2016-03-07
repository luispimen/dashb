package com.jcg.examples.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lpimentel on 02-03-2016.
 */
public class SpringProxy {
    private static ApplicationContext applicationContext;
    private static String CONTEXT_PATH = "classpath:com/nitinsurana/lenadena/spring-context.xml";

    private SpringProxy() {
    }

    public static synchronized ApplicationContext getContext() {
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext(CONTEXT_PATH);
        }
        return applicationContext;
    }
}

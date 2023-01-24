package com.home.bobocode;

import com.home.bobocode.context.ApplicationContext;
import com.home.bobocode.context.ApplicationContextImpl;
import com.home.bobocode.service.TestService;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContextImpl("com.home.bobocode");
        TestService testService = context.getBean(TestService.class);
        testService.test();
    }
}

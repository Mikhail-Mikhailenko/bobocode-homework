package com.home.bobocode.service;

import com.home.bobocode.annotations.Autowired;
import com.home.bobocode.annotations.Bean;

@Bean()
public class TestService {

    @Autowired
    private AutowiredTestService testService;

    public void test() {
        testService.print();
    }
}

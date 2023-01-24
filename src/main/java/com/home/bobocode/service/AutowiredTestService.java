package com.home.bobocode.service;

import com.home.bobocode.annotations.Bean;

@Bean
public class AutowiredTestService {

    public void print() {
        System.out.println("Autowired successful");
    }
}

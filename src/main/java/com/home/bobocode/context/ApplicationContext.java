package com.home.bobocode.context;

import java.util.List;

public interface ApplicationContext {

    <T> T getBean(Class<T> beanType);

    <T> T getBean(String name, Class<T> beanType);

    <T> List<T> getAllBeans(Class<T> beanType);
}

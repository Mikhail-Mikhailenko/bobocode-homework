package com.home.bobocode.context;

import com.home.bobocode.annotations.Autowired;
import com.home.bobocode.annotations.Bean;
import com.home.bobocode.exceptions.InitializeBeanException;
import com.home.bobocode.exceptions.InjectFieldException;
import com.home.bobocode.exceptions.NoSuchBeanException;
import com.home.bobocode.exceptions.NoUniqueBeanException;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ApplicationContextImpl implements ApplicationContext {

    private final Map<String, Object> beans = new ConcurrentHashMap<>();

    public ApplicationContextImpl(String path) {
        init(path);
    }

    private void init(String path) {
        Reflections reflections = new Reflections(path);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Bean.class);
        classes.forEach(this::createBean);
        injectBeans();
    }

    private void injectBeans() {
        beans.values().forEach(obj -> {
            List<Field> fields = List.of(obj.getClass().getDeclaredFields());
            fields.forEach(field -> {
                if (Objects.nonNull(field.getAnnotation(Autowired.class))) {
                     Object bean = getBean(field.getType());
                     field.setAccessible(true);
                    try {
                        field.set(obj, bean);
                    } catch (IllegalAccessException e) {
                        throw new InjectFieldException(e.getMessage(), e.getCause());
                    }
                }
            });
        });
    }

    private void createBean(Class<?> clazz) {
        try {
            var constructor = clazz.getConstructor();
            var bean = constructor.newInstance();
            var beanName = getBeanName(clazz);
            beans.put(beanName, bean);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new InitializeBeanException(e.getMessage(), e.getCause());
        }
    }

    private String getBeanName(Class<?> clazz) {
        Bean annotationBean = clazz.getAnnotation(Bean.class);
        if (annotationBean.value().length() > 0) {
            return annotationBean.value();
        }
        var simpleName = clazz.getSimpleName();
        return formatBeanName(simpleName);
    }

    private String formatBeanName(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public <T> T getBean(Class<T> beanType) {
        List<Object> objects = beans.values().stream()
                .filter(object -> object.getClass().equals(beanType))
                .collect(Collectors.toList());
        if (objects.isEmpty()) {
            throw new NoSuchBeanException("Does not exist bean with " + beanType.getSimpleName() + " class");
        }
        if (objects.size() > 1) {
            throw new NoUniqueBeanException("Bean with " + beanType.getSimpleName() + " must not contain several instances");
        }
        return beanType.cast(objects.get(0));
    }

    public <T> T getBean(String name, Class<T> beanType) {
        Object bean = beans.get(name);
        if (Objects.isNull(bean)) {
            throw new NoSuchBeanException("Bean does not exist with name: " + name);
        }
        return beanType.cast(bean);
    }

    public <T> List<T> getAllBeans(Class<T> beanType) {
        return beans.values().stream()
                .filter(bean -> bean.getClass().equals(bean))
                .map(beanType::cast)
                .collect(Collectors.toList());
    }
}

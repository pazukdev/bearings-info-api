package com.pazukdev.backend.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Siarhei Sviarkaltsau
 */
@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(final ApplicationContext appContext) throws BeansException {
        context = appContext;
    }

    public static <T> T getBean(final Class<T> beanClass) {
        return context.getBean(beanClass);
    }

}

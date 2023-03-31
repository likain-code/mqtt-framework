package org.yzu.cloud.configure;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yzu.cloud.common.BeanFactoryWrapper;

@Configuration
public class BeanFactoryConfiguration {

    @Bean
    public BeanFactoryWrapper beanFactoryWrapper(DefaultListableBeanFactory defaultListableBeanFactory) {
        return new BeanFactoryWrapper(defaultListableBeanFactory);
    }
}

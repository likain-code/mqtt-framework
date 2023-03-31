package org.yzu.cloud.common;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Arrays;

public class BeanFactoryWrapper {

    private final DefaultListableBeanFactory beanFactory;

    public BeanFactoryWrapper(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object getBean(String beanName) {
        return this.beanFactory.getBean(beanName);
    }

    public <T> T getBean(Class<T> clazz) {
        return this.beanFactory.getBean(clazz);
    }

    public <T> T getBean(String beanName, Class<T> clazz) {
        return this.beanFactory.getBean(beanName, clazz);
    }

    public void removeBeanDefinition(String beanName) {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    public void registerBeanDefinition(String beanName, Class<?> clazz, Object... constructorArgs) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        Arrays.asList(constructorArgs).forEach(builder::addConstructorArgValue);
        this.beanFactory.registerBeanDefinition(beanName, builder.getRawBeanDefinition());
    }
}

package org.yzu.cloud.configure;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.yzu.cloud.common.BeanFactoryWrapper;
import org.yzu.cloud.common.MqttProperties;
import org.yzu.cloud.core.InitializeHandler;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MqttProperties.class)
public class MqttConfiguration implements InitializingBean {

    private final MqttProperties mqttProperties;
    private final BeanFactoryWrapper beanFactoryWrapper;
    ExecutorService service = Executors
            .newFixedThreadPool(1, new MqttThreadFactory(1));
    private Future<Boolean> future;

    private static class MqttThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber;

        public MqttThreadFactory(int threadNumber) {
            this.threadNumber = new AtomicInteger(threadNumber);
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "mqtt-init-exec" + threadNumber.getAndIncrement());
        }
    }

    public MqttConfiguration(MqttProperties mqttProperties, BeanFactoryWrapper beanFactoryWrapper) {
        this.mqttProperties = mqttProperties;
        this.beanFactoryWrapper = beanFactoryWrapper;
    }

    @Bean
    @ConditionalOnProperty(value = "mqtt.enable", havingValue = "true")
    public MqttClient mqttClient() {
        MqttClient mqttClient;
        try {
            mqttClient = new MqttClient(mqttProperties.getHost(), mqttProperties.getClientId(), null);
        } catch (MqttException e) {
            throw new RuntimeException(
                    "Mqtt failure occurred when attempted to initialize client, client id: " + mqttProperties.getClientId(), e);
        }
        return mqttClient;
    }

    @PostConstruct
    public void mqttInitialize() {
        log.debug("Mqtt initialization task will be executed later");
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("Mqtt initialization task executing");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void executeMqttInitialization() {
        future = service.submit(new InitializeHandler(mqttProperties, beanFactoryWrapper));
        try {
            if (future.get()) {
                if (future.get()) {
                    log.debug("Mqtt initialization task finished, service is ready now");
                } else {
                    log.error("Failed to execute mqtt initialization task");
                }
            }
        } catch (Exception e) {
            log.error("UnExpected failure occurred when execute mqtt initialization task", e);
        } finally {
            service.shutdown();
        }
    }
}

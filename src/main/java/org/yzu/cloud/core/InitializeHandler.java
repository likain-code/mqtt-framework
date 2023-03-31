package org.yzu.cloud.core;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.yzu.cloud.common.BeanFactoryWrapper;
import org.yzu.cloud.common.MqttProperties;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class InitializeHandler implements Callable<Boolean> {

    private final MqttProperties mqttProperties;
    private final BeanFactoryWrapper beanFactoryWrapper;

    public InitializeHandler(MqttProperties mqttProperties, BeanFactoryWrapper beanFactoryWrapper) {
        this.mqttProperties = mqttProperties;
        this.beanFactoryWrapper = beanFactoryWrapper;
    }

    public static void connect(MqttClient mqttClient, MqttProperties mqttProperties) throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();

        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setConnectionTimeout(mqttProperties.getTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepAlive());

        mqttClient.connect(options);
    }

    @Override
    public Boolean call() {
        MqttClient mqttClient;
        try {
            mqttClient = beanFactoryWrapper.getBean(MqttClient.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.debug("Mqtt is unable now");
            return false;
        }

        try {
            connect(mqttClient, mqttProperties);
            log.debug("Mqtt client [{}] connected to broker [{}]",
                    mqttProperties.getClientId(), mqttProperties.getHost().substring(6));
        } catch (MqttException e) {
            log.error("Mqtt failure occurred when attempted to connect to broker", e);
            return false;
        }

        AtomicBoolean subscribeFlag = new AtomicBoolean(true);
        Arrays.stream(mqttProperties.getTopic()).forEach(s -> {
            try {
                mqttClient.subscribe(s);
                log.debug("Mqtt topic [{}] subscribed", s);
            } catch (MqttException e) {
                log.error("Mqtt failure occurred when attempted to subscribe topic [{}]", s, e);
                subscribeFlag.set(false);
            }
        });

        if (!subscribeFlag.get()) {
            return false;
        }

        mqttClient.setCallback(new CallbackHandler(mqttClient, mqttProperties, beanFactoryWrapper));
        return true;
    }
}

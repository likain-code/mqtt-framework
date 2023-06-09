package org.yzu.cloud.core;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.yzu.cloud.common.BeanFactoryWrapper;
import org.yzu.cloud.common.MqttProperties;
import org.yzu.cloud.exception.MqttException;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class CallbackHandler implements MqttCallback {

    private MqttClient mqttClient;
    private final MqttProperties mqttProperties;
    private final AbstractMessageProcessor messageProcessor;
    private final BeanFactoryWrapper beanFactoryWrapper;

    public CallbackHandler(MqttClient mqttClient, MqttProperties mqttProperties, BeanFactoryWrapper beanFactoryWrapper) {
        this.mqttClient = mqttClient;
        this.mqttProperties = mqttProperties;
        this.beanFactoryWrapper = beanFactoryWrapper;
        this.messageProcessor = this.beanFactoryWrapper.getBean(AbstractMessageProcessor.class);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("Connection of mqtt client [{}] to broker [{}] has lost",
                mqttProperties.getClientId(), mqttProperties.getHost(), throwable);
        log.debug("Ready to reconnect to broker [{}]", mqttProperties.getHost());

        beanFactoryWrapper.removeBeanDefinition("mqttClient");
        beanFactoryWrapper.registerBeanDefinition(
                "mqttClient", MqttClient.class, mqttProperties.getHost(), mqttProperties.getClientId(), null);
        this.mqttClient = beanFactoryWrapper.getBean(MqttClient.class);

        try {
            InitializeHandler.connect(mqttClient, mqttProperties);
            log.debug("Mqtt client [{}] reconnected to broker [{}]", mqttProperties.getClientId(), mqttProperties.getHost());
        } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
            throw new MqttException("Mqtt failure occurred when attempted to reconnect to broker", e);
        }

        String[] topics = mqttProperties.getTopic();
        for (String topic : topics) {
            try {
                mqttClient.subscribe(topic);
                log.debug("Mqtt topic [{}] resubscribed", topics);
            } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
                throw new MqttException("Mqtt failure occurred when attempted to resubscribe topics", e);
            }
        }

        mqttClient.setCallback(this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        messageProcessor.process(topic, mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // if (iMqttDeliveryToken.isComplete()) {
        //     try {
        //         log.debug("Message [{}] delivery completed", new String(iMqttDeliveryToken.getMessage().getPayload()));
        //     } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
        //         throw new MqttException("Something wrong when mqtt message delivery", e);
        //     }
        // }
    }
}

package org.yzu.cloud.core;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public abstract class AbstractMessageProcessor {

    protected void process(String topic, MqttMessage mqttMessage) {}
}

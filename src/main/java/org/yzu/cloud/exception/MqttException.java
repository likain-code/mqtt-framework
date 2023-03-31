package org.yzu.cloud.exception;


public class MqttException extends RuntimeException {

    public MqttException(String message) {
        super(message);
    }

    public MqttException(String message, Throwable cause) {
        super(message, cause);
    }
}

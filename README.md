# mqtt-framework

**mqtt-framework是一个MQTT通用框架，封装了一系列诸如MQTT连接、消息接收等方法，简单易用，仅需在pom引入依赖，并在配置文件中进行相关配置即可轻松上手使用。**



步骤：

**一、代码克隆至本地**

​    `git clone https://github.com/likain-code/mqtt-framework.git`

**二、进入代码工作区**

​	   `cd mqtt-framework/`

**三、使用maven指令打包下载至本地仓库**(请确保maven环境变量已配置好，否则会找不到mvn命令)

​  	 `mvn clean install`

**四、将依赖引入自己的springboot项目**

`<dependency>`

  `<groupId>org.yzu.cloud</groupId>`

  `<artifactId>mqtt-framework</artifactId>`

  `<version>1.0.0</version>`

`</dependency>`

**五、具体使用** 

1. 开始

2. 配置文件格式(以properties文件为例)

   `mqtt.enable=true  # 是否开启mqtt功能`

   `mqtt.host=tcp://xx.xx.xx.xx:xxxx  # mqtt服务器的URL`

   `mqtt.username=root  # 连接用户名`

   `mqtt.password=password  # 连接密码`

   `mqtt.timeout=10  # 连接超时时间（单位s）`

   `mqtt.keepAlive=60  # 保持连接状态时间（单位s）`

   `mqtt.topic[x]=xxxx  # 想要订阅的主题，数组形式`

3. MQTT连接

   无需连接，类似于tomcat服务器，配置文件写好后启动项目即可自动完成与MQTT服务器的连接。

4. MQTT断线重连接

   发生异常时，MQTT服务器会断开与自己服务器的连接，本框架会自动完成重新连接，无需自己配置。

5. MqttClient对象获取

   该框架在正确地连接MQTT服务器后，会自动生成一个mqttClient对象并注入bean容器中，只需将其从容器中获取出来即可。

6. 消息发布

   使用mqttClient对象的publish方法完成消息发布。

7. 消息接收

   写一个处理器类继承AbstractMessageProcessor类并重写process方法，最后将该类注入bean容器即可。其中topic参数为消息来自哪个主题，mqttMessage参数可获取具体消息体。

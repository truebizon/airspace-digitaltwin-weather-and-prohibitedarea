package com.hitachi.droneroute.fpadl.mqtt;

import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

import javax.net.ssl.SSLContext;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.fpadl.dto.mqtt.FlightProhibitedAreaError;

import lombok.RequiredArgsConstructor;

/**
 * MQTT publisher.
 *
 * @author soichi.kimura
 */
@Component
@RequiredArgsConstructor
public class MqttBrokerPublisher {
    /** ロガー. */
	private static final Logger LOG = LoggerFactory.getLogger(MqttBrokerPublisher.class);
    /** MQブローカーのURL. */
    @Value("${mqtt.broker.url}")
	private String url;
    /** MQブローカーに接続する際のユーザ名. */
    @Value("${mqtt.broker.userName}")
	private String userName;
    /** MQブローカーに接続する際のパスワード. */
    @Value("${mqtt.broker.password}")
	private String password;
    /** MQブローカーに接続する際のクライアントId. */
    @Value("${mqtt.broker.clientId}")
	private String clientId;

    /** MQブローカーにpublishする際のQOS. */
    @Value("${mqtt.broker.qos}")
	private Integer qos;
    /** エラー通知の宛先administratorId. */
    @Value("${mqtt.broker.administratorId}")
	private String administratorId;
    /** エラー通知topic名. */
    @Value("${mqtt.broker.topicPublishError}")
	private String topicPublishError;

    /** jacksonのObjectMapper. */
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * エラー通知をMQブローカーにpublishする.<br>
	 * 　publishできない場合、ログに警告を出力
	 * @param dto エラー通知DTO
	 */
	public void publishError(FlightProhibitedAreaError dto) {
		String json;
		try {
			json = objectMapper.writeValueAsString(dto);
			publish(topicPublishError.replace("{administratorId}", administratorId), json);
			LOG.info(MessageFormat.format("エラー通知 publish：" + "message={0}", json));
		} catch (Exception e) {
			LOG.error("エラー通知のpublishに失敗しました。", e);
		}
	}
	
	/**
	 * メッセージをpublishする
	 * @param publishTopic　トピック名
	 * @param publishMessage 送信するメッセージ
	 * @throws NoSuchAlgorithmException アルゴリズムなし例外
	 * @throws MqttException MQTT例外
	 * @throws MqttSecurityException MQTTセキュリティ例外
	 */
	private void publish(String publishTopic, String publishMessage) throws MqttSecurityException, MqttException, NoSuchAlgorithmException {

		MqttClient mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
		
		MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(false);
        connOpts.setUserName(userName);
        connOpts.setPassword(password.toCharArray());
        connOpts.setSocketFactory(SSLContext.getDefault().getSocketFactory());

        mqttClient.connect(connOpts);

        MqttMessage message = new MqttMessage(publishMessage.getBytes());
        message.setQos(qos);
        mqttClient.publish(publishTopic, message);

        mqttClient.disconnect();
        mqttClient.close();
	}
}

package br.com.tsg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class RabbitService implements AutoCloseable{

    private final Connection connection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RabbitService(
            @Value("${spring.rabbitmq.host}") String rabbitmqHost,
            @Value("${spring.rabbitmq.username}") String rabbitmqUsername,
            @Value("${spring.rabbitmq.password}") String rabbitmqPassword) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        log.info("Credentials for rabbitmq connection: {}, {}", rabbitmqUsername, rabbitmqPassword);
        factory.setHost(rabbitmqHost);
        factory.setUsername(rabbitmqUsername);
        factory.setPassword(rabbitmqPassword);
        factory.setRequestedHeartbeat(30);
        connection = factory.newConnection();
    }

    public <T> void registerConsumer(String queueName, Class<T> messageType, MessageHandler<T> handler) throws IOException {
        try {
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicConsume(queueName, true, (consumerTag, message) -> {
                try {
                    String body = new String(message.getBody(), StandardCharsets.UTF_8);
                    T deserializedMessage = new ObjectMapper().readValue(body, messageType); // Deserialize the message
                    handler.handle(deserializedMessage); // Process the message
                } catch (Exception e) {
                    log.error("Error while handling message: {}", e.getMessage(), e);
                }
            }, consumerTag -> log.info("Consumer {} cancelled", consumerTag));
        } catch (Exception e) {
            log.error("Error while registering new consumer: ", e);
        }
    }

    public <T> void publishMessage(String queueName, T message) throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        try{
            String messageJson = this.objectMapper.writeValueAsString(message);

            channel.basicPublish("", queueName, null, messageJson.getBytes(StandardCharsets.UTF_8));
            log.info("Message published to queue [{}]: {}", queueName, messageJson);
        } catch (JsonProcessingException e) {
            throw new IOException("Error while publishing message.", e);
        }
    }

    @Override
    public void close() throws IOException, TimeoutException {
        if(connection != null && connection.isOpen()){
            connection.close();
        }
    }

    @FunctionalInterface
    protected interface MessageHandler<T> {
        void handle(T message) throws Exception;
    }
}

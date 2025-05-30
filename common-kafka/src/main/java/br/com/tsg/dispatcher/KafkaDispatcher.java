package br.com.tsg.dispatcher;

import br.com.tsg.collections.CorrelationId;
import br.com.tsg.collections.KafkaMessage;
import br.com.tsg.serializer.GsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class KafkaDispatcher<T> implements Closeable {
    private final KafkaProducer<String, KafkaMessage<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); //TODO: ADD VALUE ANNOTATION
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    public void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = sendAsync(topic, key, id, payload);
        future.get();
    }

    private Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId id, T payload) {
        var value = new KafkaMessage<>(payload.getClass().getName(), id, payload);
        var record = new ProducerRecord<>(topic, key, value);
        Callback callback = (data, exception) -> {
            if (exception != null) exception.printStackTrace();
            log.info("Sending message to topic: {} :::partition {} /offset {} /timestamp {}", topic, data.partition(), data.offset(), data.timestamp());
        };

        return producer.send(record, callback);
    }

    @Override
    public void close() {
        producer.close();
    }
}

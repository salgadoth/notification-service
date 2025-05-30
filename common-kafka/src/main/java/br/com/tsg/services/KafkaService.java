package br.com.tsg.services;

import br.com.tsg.collections.KafkaMessage;
import br.com.tsg.consumer.ConsumerFunction;
import br.com.tsg.deserializer.GsonDeserializer;
import br.com.tsg.dispatcher.KafkaDispatcher;
import br.com.tsg.serializer.GsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

@Slf4j
public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, KafkaMessage<T>> consumer;
    private final ConsumerFunction<T> parse;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(parse, groupId, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(parse, groupId, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(ConsumerFunction<T> parse, String groupId, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, properties));
    }

    private Properties getProperties(String groupId, Map<String, String> overrideProperties) {
        var newProperties = new Properties();
        newProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        newProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        newProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        newProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        newProperties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        newProperties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        newProperties.putAll(overrideProperties);

        return  newProperties;
    }

    public void run() throws ExecutionException, InterruptedException {
        try(var deadLetter = new KafkaDispatcher<>()){
            while(true) {
                var records = consumer.poll(Duration.ofMillis(100));
                if(!records.isEmpty()) {
                    log.info("Encontrei {} registros", records.count());
                    for (var record : records) {
                        try{
                            parse.consume(record);
                        } catch (Exception e) {
                            log.error("Error while processing record: {}", record, e);
                            e.printStackTrace();
                            var message = record.value();
                            deadLetter.send("NEW_MESSAGE_DEAD_LETTER", message.getId().toString(), message.getId().continueWith("DeadLetter"), new GsonSerializer().serialize("", message));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        consumer.close();
    }
}

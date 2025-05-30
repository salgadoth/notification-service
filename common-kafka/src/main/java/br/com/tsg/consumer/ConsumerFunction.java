package br.com.tsg.consumer;

import br.com.tsg.collections.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {
    void consume(ConsumerRecord<String, KafkaMessage<T>> record) throws Exception;
}

package br.com.tsg.collections;

import lombok.Getter;

@Getter
public class KafkaMessage<T> {
    private String type;
    private final CorrelationId id;
    private final T payload;

    public KafkaMessage(String type, CorrelationId id, T payload) {
        this.type = type;
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                "id=" + id +
                "payload=" + payload +
                "}";
    }
}

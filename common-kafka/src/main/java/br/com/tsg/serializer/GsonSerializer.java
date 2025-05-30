package br.com.tsg.serializer;

import br.com.tsg.adapters.MessageAdapter;
import br.com.tsg.collections.KafkaMessage;
import br.com.tsg.consumer.ConsumerFunction;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.lang.reflect.Type;

public class GsonSerializer<T> implements Serializer<T> {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(KafkaMessage.class, new MessageAdapter<T>()).create();


    @Override
    public byte[] serialize(String topic, T data) {
        return gson.toJson(data).getBytes();
    }
}

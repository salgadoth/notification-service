package br.com.tsg.adapters;

import br.com.tsg.collections.CorrelationId;
import br.com.tsg.collections.KafkaMessage;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter<T> implements JsonSerializer<KafkaMessage<T>>, JsonDeserializer<KafkaMessage<T>> {
    @Override
    public KafkaMessage<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var obj = json.getAsJsonObject();
        var payloadType = obj.get("type").getAsString();
        var correlationId = (CorrelationId) context.deserialize(obj.get("correlationId"), CorrelationId.class);
        try {
            var payload = context.deserialize(obj.get("payload"), Class.forName(payloadType));
            return new KafkaMessage(payloadType, correlationId, payload);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonElement serialize(KafkaMessage message, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", message.getPayload().getClass().getName());
        jsonObject.add("payload", context.serialize(message.getPayload()));
        jsonObject.add("correlationId", context.serialize(message.getId()));
        return jsonObject;
    }
}

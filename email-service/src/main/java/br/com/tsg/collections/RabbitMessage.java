package br.com.tsg.collections;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RabbitMessage {
    private CorrelationId correlationId;
    private Message payload;
}

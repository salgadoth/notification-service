package br.com.tsg;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitConsumerWorker<T> implements Runnable, Closeable {
    private final RabbitService rabbitService;
    private final String queueName;
    private final Class<T> messageType;
    private final RabbitService.MessageHandler<T> handler;
    private volatile boolean running = true;

    public RabbitConsumerWorker(RabbitService rabbitService, String queueName, Class<T> messageType, RabbitService.MessageHandler<T> handler) {
        this.rabbitService = rabbitService;
        this.queueName = queueName;
        this.messageType = messageType;
        this.handler = handler;
    }

    public void start() throws Exception {
        Thread workerThreaad = new Thread(this, "RabbitConsumerWorker - " + queueName);
        workerThreaad.start();
        log.info("Rabbit consumer worker started for queue: {}", queueName);
    }

    public void stop() {
        running = false;
        log.info("RabbitMQ consumer worker stopped for queue: {}", queueName);
    }

    @Override
    public void close() {
        stop();
        try {
            rabbitService.close();
        } catch (IOException | TimeoutException e) {
            log.error("Error while closing RabbitMQ service: {}", e.getMessage());
        }
    }

    @Override
    public void run() {
        while(running) {
            try {
                rabbitService.registerConsumer(queueName, messageType, handler);
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Error in RabbitMQ consumer worker: ", e);
            }
        }
    }
}

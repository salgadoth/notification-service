package br.com.tsg;

import br.com.tsg.collections.RabbitMessage;
import br.com.tsg.exceptions.EmailSendingException;
import br.com.tsg.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class EmailServiceApplication {
    private final MessageService messageService;

    public EmailServiceApplication(MessageService messageService) {
        this.messageService = messageService;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(EmailServiceApplication.class);
        EmailServiceApplication emailServiceApplication = ctx.getBean(EmailServiceApplication.class);

        try {
            RabbitService rabbitService = new RabbitService();

            RabbitConsumerWorker<RabbitMessage> rabbitConsumerWorker = new RabbitConsumerWorker<>(
                    rabbitService,
                    "QUEUE_NEW_MESSAGE",
                    RabbitMessage.class,
                    emailServiceApplication::parse
            );

            rabbitConsumerWorker.start();
            log.info("RabbitMQ consumer started, waiting for messages...");

            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> {
                        try {
                            rabbitConsumerWorker.close();
                            log.info("Application shutdown gracefully.");
                        } catch (Exception e) {
                            log.error("Error during shutdown: {}", e.getMessage());
                        }
                    })
            );
        } catch (Exception e) {
            log.error("Failed to start RabbitMQ consumer", e);
        }
    }

    private void parse(RabbitMessage rabbitMessage) throws EmailSendingException {
        log.info("Processing new message: {}", rabbitMessage.getCorrelationId());
        messageService.processMessage(rabbitMessage.getPayload());
    }
}

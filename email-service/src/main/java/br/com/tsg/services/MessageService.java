package br.com.tsg.services;

import br.com.tsg.exceptions.EmailSendingException;
import br.com.tsg.collections.Message;
import br.com.tsg.repositories.MessageRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service @Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmailService emailService;

    public MessageService(final MessageRepository repository, EmailService emailService) {
        this.messageRepository = repository;
        this.emailService = emailService;
    }

    public void processMessage(Message message) throws EmailSendingException {
        log.info("Processing new message...");
        Optional<Message> optMessage = messageRepository.findById(new ObjectId(message.getId()));
        if(optMessage.isPresent()){
            Message messageDocument = optMessage.get();

            try {
                emailService.sendEmail(message.getEmail(), "New contact form entry", message.getMessage());
            } catch (MessagingException | IOException | GeneralSecurityException e) {
                throw new EmailSendingException("Error during the process of sending the email: {}" + e.getMessage(), e);
            }
            log.info("Message sent, updating its status and persisting...");
            messageDocument.setNotified(true);
            messageRepository.save(messageDocument);
            log.info("Message status persisted.");
        } else {
            log.error("Message with id: {} not found.", message.getId());
        }
    }
}

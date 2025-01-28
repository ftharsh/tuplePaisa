package org.harsh.tuple.paisa.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void testSendEmail() throws NoSuchFieldException, IllegalAccessException {

        JavaMailSender mockMailSender = Mockito.mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mockMailSender);

        String fromEmailId = "noreply@example.com";
        String recipient = "testuser@example.com";
        String subject = "Test Subject";
        String body = "This is a test email.";

        Field fromEmailField = emailService.getClass().getDeclaredField("fromEmailId");
        fromEmailField.setAccessible(true);
        fromEmailField.set(emailService, fromEmailId);


        emailService.sendEmail(recipient, body, subject);


        ArgumentCaptor<SimpleMailMessage> mailMessageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(1)).send(mailMessageCaptor.capture());

        SimpleMailMessage capturedMessage = mailMessageCaptor.getValue();


        assertEquals(fromEmailId, capturedMessage.getFrom());
        assertEquals(recipient, capturedMessage.getTo()[0]);
        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(body, capturedMessage.getText());
    }
}
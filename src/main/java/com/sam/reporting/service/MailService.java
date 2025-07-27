package com.sam.reporting.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mail;

    public void sendPlain(String to, String subject, String body) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo(to);
        m.setSubject(subject);
        m.setText(body);
        mail.send(m);
    }

    public void sendWithAttachment(String to,
                                   String subject,
                                   String body,
                                   byte[] bytes,
                                   String filename) {

        try {
            MimeMessage msg = mail.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            helper.addAttachment(filename, new ByteArrayResource(bytes));

            mail.send(msg);

        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send e‑mail with attachment", ex);
        }
    }
}

package com.sofiatech.kdg.services;
import com.sofiatech.kdg.interfaces.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
@Service
@AllArgsConstructor
@Slf4j
public class EmailService implements EmailSender {


    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("contact@kdg.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send email " + e);

            throw new IllegalStateException("failed to send email");
        }
    }
}
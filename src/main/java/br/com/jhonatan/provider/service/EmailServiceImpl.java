package br.com.jhonatan.provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;


    @Value("${spring.mail.username}") // environment variable configured in ide
    private String FROM;

    @Override
    public void sendSubscriptionConfirmation(String toEmail, String customerName, String subscriptionCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(toEmail);
        message.setSubject("Confirmed subscription");
        message.setText("Hello " + customerName + ",\n\n"
                + "Your subscription " + subscriptionCode + " was activated with successful.\n\n"
                + "Thank you for signing.");

        try {
            mailSender.send(message);
            log.info("Confirmation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendSubscriptionCancellation(String toEmail, String customerName, String subscriptionCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(toEmail);
        message.setSubject("Canceled subscription");
        message.setText("Hello " + customerName + ",\n\n"
                + "Your subscription " + subscriptionCode + " was cancelled.\n\n"
                + "We will miss you.");

        try {
            mailSender.send(message);
            log.info("Cancellation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send cancellation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendSubscriptionReactivation(String toEmail, String customerName, String subscriptionCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(toEmail);
        message.setSubject("Reactivated subscription");
        message.setText("Hello " + customerName + ",\n\n"
                + "Your subscription " + subscriptionCode + " was reactivated.\n\n"
                + "We will miss you.");

        try {
            mailSender.send(message);
            log.info("Reactivation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send reactivation email to {}: {}", toEmail, e.getMessage());
        }
    }
}
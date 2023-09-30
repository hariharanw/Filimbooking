package com.bus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendBookingConfirmationEmail(String to, String subject, String customerName,
                                             String movieTitle, String theater, String bookingDate,
                                             String bookingTime, String seatNumbers) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Prepare the Thymeleaf context with dynamic content
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("movieTitle", movieTitle);
            context.setVariable("theater", theater);
            context.setVariable("bookingDate", bookingDate);
            context.setVariable("bookingTime", bookingTime);
            context.setVariable("seatNumbers", seatNumbers);

            // Process the Thymeleaf template to generate the email content
            String emailContent = templateEngine.process("booking-confirmation", context);

            // Set email details
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true); // 'true' indicates it's HTML content

            // Send the email
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

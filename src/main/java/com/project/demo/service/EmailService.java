package com.project.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String email, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(email);
            helper.setSubject("Verify OTP");
            helper.setText("Your verification OTP is: " + otp);

            javaMailSender.send(message);
            System.out.println("✅ Email sent successfully to " + email);
        } catch (MailException e) {
            System.out.println("⚠️ Email send failed: " + e.getMessage());
            throw new MailSendException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

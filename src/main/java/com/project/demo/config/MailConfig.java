package com.project.demo.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // ✅ Default to Gmail SMTP
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(System.getenv("MAIL_USERNAME") != null 
                ? System.getenv("MAIL_USERNAME") 
                : "youremail@gmail.com"); // fallback
        mailSender.setPassword(System.getenv("MAIL_PASSWORD") != null 
                ? System.getenv("MAIL_PASSWORD") 
                : "your-app-password"); // fallback

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true");

        return mailSender;
    }
}

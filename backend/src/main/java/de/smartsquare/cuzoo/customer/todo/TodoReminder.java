package de.smartsquare.cuzoo.customer.todo;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class TodoReminder {
    final JavaMailSender sender;

    public TodoReminder(JavaMailSender sender) {
        this.sender = sender;
    }

    void send(String to, String subject, String text) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        sender.send(message);
    }

}

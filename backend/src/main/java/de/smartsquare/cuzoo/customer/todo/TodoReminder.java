package de.smartsquare.cuzoo.customer.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class TodoReminder {
    private final JavaMailSender sender;
    private final TaskScheduler taskScheduler;
    private final TodoRepository todoRepository;

    private static final Logger log = LoggerFactory.getLogger(TodoReminder.class);

    public TodoReminder(JavaMailSender sender, TaskScheduler taskScheduler, TodoRepository todoRepository) {
        this.sender = sender;
        this.taskScheduler = taskScheduler;
        this.todoRepository = todoRepository;
    }

    private void send(String to, String subject, String text) throws MailException, MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        sender.send(message);
    }

    @Scheduled(fixedRate = 1800000) // every 30 minutes
    void sendTodoReminder() {
        List<Todo> undoneTodos = todoRepository.findAllByDoneFalseAndScheduledFalse();

        undoneTodos.forEach(todo -> {
            Date now = new Date(System.currentTimeMillis());

            if (todo.getReminder().after(now)) {
                taskScheduler.schedule(() -> {
                    String to = todo.getCreator().getMail();
                    String companyName = todo.getCompany().getName();
                    String subject = "[CuZoo] TODO für " + companyName + " fällig";

                    try {
                        send(to, subject, getMailMessage(todo));
                    } catch (MailException | MessagingException e) {
                        log.error("Error sending mail", e);
                    }
                }, todo.getReminder());
            }

            todo.setScheduled(true);
            todoRepository.save(todo);
        });
    }

    String getMailMessage(Todo todo) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.");
        String faelligkeitsdatum = format.format(todo.getExpiration());
        String firstname = todo.getCreator().getFullname().split(" ")[0];
        String companyName = todo.getCompany().getName();

        return "Hey " + firstname + "!\n\n" +
                "Dein TODO für das Unternehmen " + companyName + " ist am " + faelligkeitsdatum + " fällig!\n\n" +
                "Beschreibung:\n" + todo.getDescription() + "\n\nLiebe Grüße\nFriendly Reminder :)\n\n" +
                "https://cuzoo.smartsquare.de";
    }

}

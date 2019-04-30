package de.smartsquare.cuzoo.customer.todo;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.user.User;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TodoReminderTest {
    private Company company = new Company("Ruby Dooby GmbH", "", "", "", "", "", "");
    private Todo todo = new Todo("Halloele", company, 888888L, 444444L);

    private final TodoReminder todoReminder = new TodoReminder(
            mock(JavaMailSender.class),
            mock(TaskScheduler.class),
            mock(TodoRepository.class)
    );

    @Test
    public void that_mail_message_contains_todo_information() throws Exception {
        todo.setCreator(new User("drs", "1234", "drs tck", "drs@t.ck"));
        String mail = todoReminder.getMailMessage(todo);

        assertThat(mail).contains(todo.getDescription());
        assertThat(mail).contains(todo.getCreator().getUsername());
        assertThat(mail).contains(company.getName());
    }
}

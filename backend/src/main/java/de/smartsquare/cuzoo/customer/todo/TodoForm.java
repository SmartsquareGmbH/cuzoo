package de.smartsquare.cuzoo.customer.todo;

import de.smartsquare.cuzoo.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class TodoForm {
    private Long id;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private Long expiration;

    @NotNull
    private Long reminder;

    private boolean done;

    @NotNull
    @NotBlank
    private String creator;

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getExpiration() {
        return expiration;
    }

    public Long getReminder() {
        return reminder;
    }

    public boolean isDone() {
        return done;
    }

    public String getCreator() {
        return creator;
    }
}

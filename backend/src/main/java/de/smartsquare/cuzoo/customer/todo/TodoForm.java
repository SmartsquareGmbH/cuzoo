package de.smartsquare.cuzoo.customer.todo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    public TodoForm(Long id, @NotNull @NotBlank String description, @NotNull Long expiration, @NotNull Long reminder,
                    boolean done, @NotNull @NotBlank String creator) {
        this.id = id;
        this.description = description;
        this.expiration = expiration;
        this.reminder = reminder;
        this.done = done;
        this.creator = creator;
    }

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

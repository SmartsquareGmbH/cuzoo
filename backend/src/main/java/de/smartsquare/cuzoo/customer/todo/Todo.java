package de.smartsquare.cuzoo.customer.todo;

import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todo_generator")
    @SequenceGenerator(name = "todo_generator", sequenceName = "todo_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @NotNull
    private Date expiration;

    @NotNull
    private Date reminder;

    private boolean done;
    private boolean scheduled;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;


    Todo() {

    }

    public Todo(@NotNull @NotBlank String description, @NotNull Company company,
                @NotNull Long expiration, @NotNull Long reminder) {
        this.description = description;
        this.company = company;
        this.expiration = new Date(expiration);
        this.reminder = new Date(reminder);

        this.done = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = new Date(expiration);
    }

    public Date getReminder() {
        return reminder;
    }

    public void setReminder(Long reminder) {
        this.reminder = new Date(reminder);
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }
}

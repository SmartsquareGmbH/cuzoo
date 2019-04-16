package de.smartsquare.cuzoo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;
import de.smartsquare.cuzoo.customer.todo.Todo;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    @SequenceGenerator(name = "users_generator", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    private String password;
    private String fullname;
    private String mail;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Contact> contacts;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ContactPoint> contactPoints;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Todo> todos;

    public User(@NotNull @NotBlank final String username, String password, String fullname, String mail) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.mail = mail;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<ContactPoint> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<ContactPoint> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }
}

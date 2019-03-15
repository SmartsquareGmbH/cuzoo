package de.smartsquare.cuzoo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contact.Contact;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    @SequenceGenerator(name = "users_generator", sequenceName = "user_seq")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    private String password;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Contact> contacts;

    public User(@NotNull @NotBlank final String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() { }

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

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}

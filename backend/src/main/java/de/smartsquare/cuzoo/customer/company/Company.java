package de.smartsquare.cuzoo.customer.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contact.Contact;
import de.smartsquare.cuzoo.customer.todo.Todo;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_generator")
    @SequenceGenerator(name = "company_generator", sequenceName = "company_seq")
    private Long id;
    @NotNull
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Contact> contacts;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Todo> todos;

    private String street;
    private String zipcode;
    private String place;
    private String homepage;
    private String status;
    private String description;
    private String other;

    public Company() {
    }

    public Company(@NotNull @NotBlank final String name,
                   String street, String zipcode, String place,
                   String homepage, String description, String other) {
        this.name = name;
        this.street = street;
        this.zipcode = zipcode;
        this.place = place;
        this.homepage = homepage;
        this.description = description;
        this.other = other;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }
}

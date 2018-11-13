package de.smartsquare.cuzoo.customer;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_generator")
    @SequenceGenerator(name ="company_generator", sequenceName = "company_seq")
    private Long id;
    @NotNull
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "company")
    @JsonBackReference
    private List<Contact> contacts;

    private String street;
    private String zipCode;
    private String place;
    private String homepage;
    private String purpose;
    private String other;

    public Company() {
    }

    Company(@NotNull @NotBlank final String name, List<Contact> contacts,
            String street, String zipCode, String place,
            String homepage, String purpose, String other) {
        this.name = name;
        this.contacts = contacts;
        this.street = street;
        this.zipCode = zipCode;
        this.place = place;
        this.homepage = homepage;
        this.purpose = purpose;
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

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}

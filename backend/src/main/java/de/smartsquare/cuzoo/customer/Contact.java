package de.smartsquare.cuzoo.customer;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_generator")
    @SequenceGenerator(name ="contact_generator", sequenceName = "contact_seq")
    private Long id;
    @NotNull
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;

    private String role;
    private String mail;
    private String telephone;
    private String lastContact;
    private String lastAnswer;
    private String comment;

    public Contact() {
    }

    Contact(@NotNull @NotBlank final String name, Company company,
            String role, String mail, String telephone,
            String lastContact, String lastAnswer, String comment) {
        this.name = name;
        this.company = company;
        this.role = role;
        this.mail = mail;
        this.telephone = telephone;
        this.lastContact = lastContact;
        this.lastAnswer = lastAnswer;
        this.comment = comment;
    }

    Contact(@NotNull @NotBlank final String name,
            String role, String mail, String telephone,
            String lastContact, String lastAnswer, String comment) {
        this.name = name;
        this.role = role;
        this.mail = mail;
        this.telephone = telephone;
        this.lastContact = lastContact;
        this.lastAnswer = lastAnswer;
        this.comment = comment;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getLastContact() {
        return lastContact;
    }

    public void setLastContact(String lastContact) {
        this.lastContact = lastContact;
    }

    public String getLastAnswer() {
        return lastAnswer;
    }

    public void setLastAnswer(String lastAnswer) {
        this.lastAnswer = lastAnswer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

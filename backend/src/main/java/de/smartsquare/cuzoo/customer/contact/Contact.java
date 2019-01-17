package de.smartsquare.cuzoo.customer.contact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    @OneToMany(mappedBy = "contact", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ContactPoint> contactPoints;

    private String role;
    private String mail;
    private String telephone;
    private String lastContact;
    private String lastAnswer;
    private String comment;

    public Contact() {
    }

    public Contact(@NotNull @NotBlank final String name, Company company,
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

    public Contact(@NotNull @NotBlank final String name,
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

    public List<ContactPoint> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<ContactPoint> contactPoints) {
        this.contactPoints = contactPoints;
    }
}

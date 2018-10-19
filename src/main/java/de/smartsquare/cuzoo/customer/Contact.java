package de.smartsquare.cuzoo.customer;

import javax.persistence.*;
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

    private String company;
    private String role;
    private String address;
    private String mail;
    private String telephone;
    private String jug;
    private String cloudLab;
    private String cioDay;
    private String cloudFlyer;
    private String lastContact;
    private String lastAnswer;
    private String comment;

    public Contact() {
    }

    Contact(@NotNull @NotBlank final String name, String company,
            String role, String address, String mail, String telephone,
            String jug, String cloudLab, String cioDay, String cloudFlyer,
            String lastContact, String lastAnswer, String comment) {
        this.name = name;
        this.company = company;
        this.role = role;
        this.address = address;
        this.mail = mail;
        this.telephone = telephone;
        this.jug = jug;
        this.cloudLab = cloudLab;
        this.cioDay = cioDay;
        this.cloudFlyer = cloudFlyer;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getJug() {
        return jug;
    }

    public void setJug(String jug) {
        this.jug = jug;
    }

    public String getCloudLab() {
        return cloudLab;
    }

    public void setCloudLab(String cloudLab) {
        this.cloudLab = cloudLab;
    }

    public String getCioDay() {
        return cioDay;
    }

    public void setCioDay(String cioDay) {
        this.cioDay = cioDay;
    }

    public String getCloudFlyer() {
        return cloudFlyer;
    }

    public void setCloudFlyer(String cloudFlyer) {
        this.cloudFlyer = cloudFlyer;
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

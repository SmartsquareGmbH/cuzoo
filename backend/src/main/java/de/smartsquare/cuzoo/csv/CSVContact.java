package de.smartsquare.cuzoo.csv;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class CSVContact {
    @CsvField(pos = 1)
    private String name;

    @CsvField(pos = 2)
    private String company;

    @CsvField(pos = 3)
    private String role;

    @CsvField(pos = 5)
    private String mail;

    @CsvField(pos = 6)
    private String telephone;

    @CsvField(pos = 11)
    private String lastContact;

    @CsvField(pos = 12)
    private String lastAnswer;

    @CsvField(pos = 13)
    private String comment;

    @CsvField(pos = 4)
    private String address;

    @CsvField(pos = 7)
    private String jug;

    @CsvField(pos = 8)
    private String cloudLab;

    @CsvField(pos = 9)
    private String cioDay;

    @CsvField(pos = 10)
    private String cloudFlyer;

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
}

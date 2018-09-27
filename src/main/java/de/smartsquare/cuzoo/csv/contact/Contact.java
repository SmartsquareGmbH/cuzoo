package de.smartsquare.cuzoo.csv.contact;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class Contact {
    @CsvField(pos = 1)
    private String name;

    @CsvField(pos = 2)
    private String company;

    @CsvField(pos = 3)
    private String role;

    @CsvField(pos = 4)
    private String address;

    @CsvField(pos = 5)
    private String mail;

    @CsvField(pos = 6)
    private String telephone;

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

    public String getTel() {
        return telephone;
    }

    public void setTel(String telephone) {
        this.telephone = telephone;
    }
}

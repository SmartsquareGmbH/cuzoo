package de.smartsquare.cuzoo.customer;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_generator")
    @SequenceGenerator(name ="company_generator", sequenceName = "company_seq")
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String street;
    private String zipCode;
    private String place;
    private String homepage;
    private String purpose;
    private String other;

    public Company() {
    }

    Company(@NotNull @NotBlank final String name,
            String street, String zipCode, String place,
            String homepage, String purpose, String other) {
        this.name = name;
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

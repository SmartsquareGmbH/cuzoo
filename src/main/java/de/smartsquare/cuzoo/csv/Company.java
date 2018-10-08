package de.smartsquare.cuzoo.csv;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class Company {
    @CsvField(pos = 1)
    private String company;

    @CsvField(pos = 2)
    private String street;

    @CsvField(pos = 3)
    private String zipCode;

    @CsvField(pos = 4)
    private String place;

    @CsvField(pos = 5)
    private String homepage;

    @CsvField(pos = 6)
    private String purpose;

    @CsvField(pos = 7)
    private String other;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

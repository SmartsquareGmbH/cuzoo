package de.smartsquare.cuzoo.customer.company;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CompanyForm {

    private Long id;
    @NotNull
    @NotBlank
    private String name;

    private String street;
    private String zipcode;
    private String place;
    private String homepage;
    private String description;
    private String other;

    private List<String> labels;

    public CompanyForm(Long id, @NotNull @NotBlank String name, String street, String zipcode, String place,
                       String homepage, String description, String other, List<String> labels) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.zipcode = zipcode;
        this.place = place;
        this.homepage = homepage;
        this.description = description;
        this.other = other;
        this.labels = labels;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getPlace() {
        return place;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getDescription() {
        return description;
    }

    public String getOther() {
        return other;
    }

    public List<String> getLabels() {
        return labels;
    }
}

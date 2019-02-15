package de.smartsquare.cuzoo.customer.label;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.company.Company;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "label_generator")
    @SequenceGenerator(name = "label_generator", sequenceName = "label_seq")
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "labels")
    private List<ContactPoint> contactPointsWithLabels;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "types")
    private List<ContactPoint> contactPointsWithTypes;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "labels")
    private List<Company> companies;

    Label() {
        this.contactPointsWithLabels = new ArrayList<>();
        this.contactPointsWithTypes = new ArrayList<>();
        this.companies = new ArrayList<>();
    }

    public Label(@NotNull @NotBlank String title) {
        this.title = title;
        this.contactPointsWithLabels = new ArrayList<>();
        this.contactPointsWithTypes = new ArrayList<>();
        this.companies = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ContactPoint> getContactPointsWithLabels() {
        return contactPointsWithLabels;
    }

    public void setContactPointsWithLabels(List<ContactPoint> contactPointsWithLabels) {
        this.contactPointsWithLabels = contactPointsWithLabels;
    }

    public void addContactPointWithLabel(ContactPoint contactPoint) {
        this.contactPointsWithLabels.add(contactPoint);
    }

    public List<ContactPoint> getContactPointsWithTypes() {
        return contactPointsWithTypes;
    }

    public void setContactPointsWithTypes(List<ContactPoint> contactPointsWithTypes) {
        this.contactPointsWithTypes = contactPointsWithTypes;
    }

    public void addContactPointTypes(ContactPoint contactPoint) { this.contactPointsWithTypes.add(contactPoint); }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public void addCompany(Company company) { this.companies.add(company); }
}

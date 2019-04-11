package de.smartsquare.cuzoo.customer.opportunity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Opportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "opportunity_generator")
    @SequenceGenerator(name = "opportunity_generator", sequenceName = "opportunity_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String state;

    private String description;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ContactPoint> contactPoints;

    public Opportunity(@NotNull @NotBlank String title, @NotNull @NotBlank String state, String description) {
        this.title = title;
        this.state = state;
        this.description = description;

        this.contactPoints = new ArrayList<>();
    }

    public Opportunity() {
        this.contactPoints = new ArrayList<>();
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ContactPoint> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<ContactPoint> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public void addContactPoint(ContactPoint contactPoint) {
        this.contactPoints.add(contactPoint);
    }
}

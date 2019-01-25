package de.smartsquare.cuzoo.customer.label;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<ContactPoint> contactPoints;

    Label() {
        this.contactPoints = new ArrayList<>();
    }

    public Label(@NotNull @NotBlank String title) {
        this.title = title;
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

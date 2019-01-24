package de.smartsquare.cuzoo.customer.label;

import de.smartsquare.cuzoo.customer.contactpoint.ContactPoint;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "label_generator")
    @SequenceGenerator(name = "label_generator", sequenceName = "label_seq")
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @ManyToOne
    @JoinColumn(name = "contactPoint_id")
    private ContactPoint contactPoint;

    Label() {
    }

    public Label (@NotNull @NotBlank String title) {
        this.title = title;
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

    public ContactPoint getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(ContactPoint contactPoint) {
        this.contactPoint = contactPoint;
    }
}

package de.smartsquare.cuzoo.customer.points;

import de.smartsquare.cuzoo.customer.contact.Contact;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class CPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "point_generator")
    @SequenceGenerator(name = "point_generator", sequenceName = "point_seq")
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    private String date;
    private String comment;

    public CPoint() {
    }

    public CPoint(@NotNull @NotBlank String title,
                  @NotNull Contact contact,
                  String date, String comment) {
        this.title = title;
        this.contact = contact;
        this.date = date;
        this.comment = comment;
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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
package de.smartsquare.cuzoo.customer.points;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.smartsquare.cuzoo.customer.contact.Contact;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cpoint_generator")
    @SequenceGenerator(name = "cpoint_generator", sequenceName = "cpoint_seq")
    private Long id;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String type;

    @NotNull
    @NotBlank
    private String date;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Column(length = 510)
    private String comment;

    @OneToMany(mappedBy = "cPoint", cascade = CascadeType.REMOVE)
    @JsonIgnore
    @Column(length = 9999999)
    private List<Attachment> files;

    public CPoint() {
    }

    public CPoint(@NotNull @NotBlank String title, @NotNull @NotBlank String type,
                  @NotNull @NotBlank String date, @NotNull Contact contact, String comment) {
        this.title = title;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<Attachment> getFiles() {
        return files;
    }

    public void setFiles(List<Attachment> files) {
        this.files = files;
    }
}